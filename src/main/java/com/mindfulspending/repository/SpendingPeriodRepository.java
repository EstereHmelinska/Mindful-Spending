package com.mindfulspending.repository;

import com.mindfulspending.entity.SpendingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpendingPeriodRepository extends JpaRepository<SpendingPeriod, Long> {

    // Find all spending periods belonging to a specific user
    List<SpendingPeriod> findAllByUserUserId(Long userId);

    // Find the spending period containing a specific date for a specific user
    Optional<SpendingPeriod> findByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long userId,
            LocalDate targetDateForStart,
            LocalDate targetDateForEnd
    );

    // Check whether a proposed period overlaps an existing period
    boolean existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long userId,
            LocalDate newEndDate,
            LocalDate newStartDate
    );

    Optional<SpendingPeriod> findBySpendingPeriodIdAndUserUserId(Long spendingPeriodId, Long userId);

    //Check if the existing period we want to update overlaps another period
    // by excluding the currently chosen period to update
    boolean existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndSpendingPeriodIdNot(
            Long userId,
            LocalDate newEndDate,
            LocalDate newStartDate,
            Long spendingPeriodId
    );
}