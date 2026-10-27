package com.mindfulspending.service.impl;

import com.mindfulspending.dto.spendingPeriod.SpendingPeriodCreateRequest;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodResponse;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodUpdateRequest;
import com.mindfulspending.entity.PeriodType;
import com.mindfulspending.entity.SpendingPeriod;
import com.mindfulspending.entity.User;
import com.mindfulspending.exception.spendingPeriod.InvalidSpendingPeriodException;
import com.mindfulspending.exception.spendingPeriod.SpendingPeriodNotFoundException;
import com.mindfulspending.exception.spendingPeriod.SpendingPeriodOverlapException;
import com.mindfulspending.exception.user.UserNotFoundException;
import com.mindfulspending.repository.SpendingPeriodRepository;
import com.mindfulspending.repository.UserRepository;
import com.mindfulspending.service.SpendingPeriodService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class SpendingPeriodServiceImpl implements SpendingPeriodService {
    private final SpendingPeriodRepository spendingPeriodRepository;
    private final UserRepository userRepository;

    public SpendingPeriodServiceImpl(SpendingPeriodRepository spendingPeriodRepository, UserRepository userRepository) {
        this.spendingPeriodRepository = spendingPeriodRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public SpendingPeriodResponse createSpendingPeriod(SpendingPeriodCreateRequest request) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        LocalDate startDate;
        LocalDate endDate;

        if (request.periodType() == PeriodType.MONTHLY) {
            if (request.month() == null || request.year() == null) {
                throw new InvalidSpendingPeriodException("Month and year are required for a monthly spending period");
            } else {
                YearMonth yearMonth = YearMonth.of(request.year(), request.month());

                startDate = yearMonth.atDay(1);
                endDate = yearMonth.atEndOfMonth();
            }
        } else {
            if (request.startDate() == null || request.endDate() == null) {
                throw new InvalidSpendingPeriodException("Start date and end date are required for a custom spending period");
            }
            startDate = request.startDate();
            endDate = request.endDate();

        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidSpendingPeriodException("Start date cannot be after end date");
        }

        if (spendingPeriodRepository.existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                user.getUserId(), endDate, startDate)) {
            throw new SpendingPeriodOverlapException("Spending period overlaps with an existing period");
        }

        SpendingPeriod spendingPeriod = SpendingPeriod.builder()
                .startDate(startDate)
                .endDate(endDate)
                .periodType(request.periodType())
                .budgetAmount(request.budgetAmount())
                .user(user)
                .build();

        SpendingPeriod saved = spendingPeriodRepository.save(spendingPeriod);

        return mapToResponse(saved);
    }

    @Transactional
    @Override
    public SpendingPeriodResponse updateSpendingPeriod(Long spendingPeriodId, SpendingPeriodUpdateRequest request) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        SpendingPeriod spendingPeriod = spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        spendingPeriodId,
                        user.getUserId())
                .orElseThrow(SpendingPeriodNotFoundException::new);

        LocalDate startDate;
        LocalDate endDate;
        if (request.periodType() == PeriodType.MONTHLY) {

            if (request.month() == null || request.year() == null) {
                throw new InvalidSpendingPeriodException("Month and year are required for a monthly spending period");
            }

            YearMonth yearMonth = YearMonth.of(request.year(), request.month());

            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();

        } else {
            if (request.startDate() == null || request.endDate() == null) {
                throw new InvalidSpendingPeriodException("Start date and end date are required for a custom spending period");
            }

            startDate = request.startDate();
            endDate = request.endDate();

        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidSpendingPeriodException("Start date cannot be after end date");
        }

        if (spendingPeriodRepository
                .existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndSpendingPeriodIdNot(
                        user.getUserId(),
                        endDate,
                        startDate,
                        spendingPeriodId)) {

            throw new SpendingPeriodOverlapException("Spending period overlaps with an existing period");
        }

        spendingPeriod.setStartDate(startDate);
        spendingPeriod.setEndDate(endDate);
        spendingPeriod.setPeriodType(request.periodType());
        spendingPeriod.setBudgetAmount(request.budgetAmount());
        SpendingPeriod updated = spendingPeriodRepository.save(spendingPeriod);
        return mapToResponse(updated);
    }

    @Override
    public List<SpendingPeriodResponse> getSpendingPeriods() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        List<SpendingPeriod> periods = spendingPeriodRepository.findAllByUserUserId(user.getUserId());
        return periods.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SpendingPeriodResponse getSpendingPeriodById(Long spendingPeriodId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        SpendingPeriod spendingPeriod = spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        spendingPeriodId,
                        user.getUserId())
                .orElseThrow(SpendingPeriodNotFoundException::new);
        return mapToResponse(spendingPeriod);
    }

    private SpendingPeriodResponse mapToResponse(SpendingPeriod spendingPeriod) {

        return SpendingPeriodResponse.builder()
                .spendingPeriodId(spendingPeriod.getSpendingPeriodId())
                .startDate(spendingPeriod.getStartDate())
                .endDate(spendingPeriod.getEndDate())
                .periodType(spendingPeriod.getPeriodType())
                .budgetAmount(spendingPeriod.getBudgetAmount())
                .build();
    }
}
