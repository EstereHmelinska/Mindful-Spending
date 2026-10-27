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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpendingPeriodServiceImplTest {

    @Mock
    private SpendingPeriodRepository spendingPeriodRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private SpendingPeriodServiceImpl spendingPeriodService;

    private User existingUser;

    @BeforeEach
    void setUp(){
        existingUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .passwordHash("12345678")
                .build();

    }


    @Nested
    class createSpendingPeriodTests{
        @Test
        void shouldCreateMonthlySpendingPeriodSuccessfully() {
            //Arrange
            SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                    9,
                    2026,
                    null,
                    null,
                    PeriodType.MONTHLY,
                    BigDecimal.valueOf(1000)

            );

            try(MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                    mockStatic(SecurityContextHolder.class)){
                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);
                when(securityContext.getAuthentication())
                        .thenReturn(authentication);
                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());
                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.of(existingUser));


            when(spendingPeriodRepository.existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    1L, LocalDate.of(2026, 9, 30),LocalDate.of(2026,9, 1)))
                    .thenReturn(false);
            SpendingPeriod existingSpendingPeriod =  SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026,9, 1))
                    .endDate(LocalDate.of(2026, 9, 30))
                    .periodType(PeriodType.MONTHLY)
                    .budgetAmount(BigDecimal.valueOf(1000))
                    .user(existingUser)
                    .build();
            when(spendingPeriodRepository.save(any(SpendingPeriod.class)))
                    .thenReturn(existingSpendingPeriod);
            //Act
           SpendingPeriodResponse response = spendingPeriodService.createSpendingPeriod(request);

            //Assert
            assertEquals(1L, response.spendingPeriodId());
            assertEquals(LocalDate.of(2026,9, 1), response.startDate());
            assertEquals(LocalDate.of(2026, 9, 30), response.endDate());
            assertEquals(PeriodType.MONTHLY,response.periodType());
            assertEquals(BigDecimal.valueOf(1000), response.budgetAmount());

            //verify
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByEmail(existingUser.getEmail());
            verify(spendingPeriodRepository).existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(existingUser.getUserId(), LocalDate.of(2026, 9, 30),
                    LocalDate.of(2026, 9, 1));
            verify(spendingPeriodRepository).save(any(SpendingPeriod.class));

}
        }

        @Test
        void shouldCreateCustomSpendingPeriodSuccessfully(){
            //Arrange
            SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                    null,
                    null,
                    LocalDate.of(2026,9, 4),
                    LocalDate.of(2026, 9,11),
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(50)

            );
            try(MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                    mockStatic(SecurityContextHolder.class)){
                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);
                when(securityContext.getAuthentication())
                        .thenReturn(authentication);
                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());
                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.of(existingUser));


                when(spendingPeriodRepository.existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        1L, LocalDate.of(2026, 9, 11),LocalDate.of(2026,9, 4)))
                        .thenReturn(false);
                SpendingPeriod existingSpendingPeriod =  SpendingPeriod.builder()
                        .spendingPeriodId(1L)
                        .startDate(LocalDate.of(2026,9, 4))
                        .endDate(LocalDate.of(2026, 9, 11))
                        .periodType(PeriodType.CUSTOM)
                        .budgetAmount(BigDecimal.valueOf(50))
                        .user(existingUser)
                        .build();
                when(spendingPeriodRepository.save(any(SpendingPeriod.class)))
                        .thenReturn(existingSpendingPeriod);

                //Act
                SpendingPeriodResponse response = spendingPeriodService.createSpendingPeriod(request);

                //Assert
                assertEquals(1L, response.spendingPeriodId());
                assertEquals(LocalDate.of(2026,9,4), response.startDate());
                assertEquals(LocalDate.of(2026,9,11), response.endDate());
                assertEquals(PeriodType.CUSTOM, response.periodType());
                assertEquals(BigDecimal.valueOf(50), response.budgetAmount());

                //verify
                verify(securityContext).getAuthentication();
                verify(authentication).getName();
                verify(userRepository).findByEmail(existingUser.getEmail());
                verify(spendingPeriodRepository).existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(existingUser.getUserId(), LocalDate.of(2026, 9, 11),
                        LocalDate.of(2026, 9, 4));
                verify(spendingPeriodRepository).save(any(SpendingPeriod.class));
            }

        }

        @Test
        void shouldThrowExceptionWhenMonthlyMonthOrYearMissing(){
            //Arrange
            SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                    null,
                    2026,
                    null,
                    null,
                    PeriodType.MONTHLY,
                    BigDecimal.valueOf(1000)

            );
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.of(existingUser));

                //Act&Assert
                InvalidSpendingPeriodException exception = assertThrows(
                        InvalidSpendingPeriodException.class,
                        () -> spendingPeriodService.createSpendingPeriod(request)
                );
                assertEquals(
                        "Month and year are required for a monthly spending period",
                        exception.getMessage()
                );
                //Verify
                verify(spendingPeriodRepository, never()).save(any());
            }

        }

        @Test
        void shouldThrowExceptionWhenCustomDatesMissing() {
            //Arrange
            SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                    null,
                    null,
                    null,
                    null,
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(1000)

            );
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.of(existingUser));
                //Act&Assert
                InvalidSpendingPeriodException exception = assertThrows(
                        InvalidSpendingPeriodException.class,
                        () -> spendingPeriodService.createSpendingPeriod(request)
                );
                assertEquals(
                        "Start date and end date are required for a custom spending period",
                        exception.getMessage()
                );
                //Verify
                verify(spendingPeriodRepository, never()).save(any());
            }


        }

        @Test
        void shouldThrowExceptionWhenStartDateAfterEndDate(){
            //Arrange
            SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                    null,
                    null,
                    LocalDate.of(2026,9,28),
                    LocalDate.of(2026,9,20),
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(1000)

            );
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.of(existingUser));
                //Act&Assert
                InvalidSpendingPeriodException exception = assertThrows(
                        InvalidSpendingPeriodException.class,
                        () -> spendingPeriodService.createSpendingPeriod(request)
                );
                assertEquals(
                        "Start date cannot be after end date",
                        exception.getMessage()
                );
                //Verify
                verify(spendingPeriodRepository, never()).save(any());
            }

        }

        @Test
        void shouldThrowExceptionWhenSpendingPeriodOverlaps(){
            //Arrange
            SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                    null,
                    null,
                    LocalDate.of(2026,9,20),
                    LocalDate.of(2026,9,28),
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(1000)

            );
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.of(existingUser));
                when(spendingPeriodRepository.existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        existingUser.getUserId(),
                        LocalDate.of(2026,9,28), LocalDate.of(2026,9,20)))
                        .thenReturn(true);
                //Act&Assert
                SpendingPeriodOverlapException exception = assertThrows(
                        SpendingPeriodOverlapException.class,
                        () -> spendingPeriodService.createSpendingPeriod(request)
                );
                assertEquals(
                        "Spending period overlaps with an existing period",
                        exception.getMessage()
                );
                //Verify
                verify(spendingPeriodRepository, never()).save(any());
            }

        }


        @Test
        void shouldThrowExceptionWhenUserNotFound(){
            //Arrange
            SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                    null,
                    null,
                    LocalDate.of(2026,9,20),
                    LocalDate.of(2026,9,28),
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(1000)

            );
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.empty());
                //Act&Assert
                UserNotFoundException exception = assertThrows(
                        UserNotFoundException.class, ()-> spendingPeriodService.createSpendingPeriod(request)
                );
                assertEquals("User not found", exception.getMessage());

                //verify
                verify(spendingPeriodRepository, never()).save(any());
            }
        }

        @Test
        void shouldAllowSpendingPeriodWithoutBudget(){
                //Arrange
                SpendingPeriodCreateRequest request = new SpendingPeriodCreateRequest(
                        null,
                        null,
                        LocalDate.of(2026,9,20),
                        LocalDate.of(2026,9,28),
                        PeriodType.CUSTOM,
                        null

                );
                try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                             mockStatic(SecurityContextHolder.class)) {

                    mockedSecurityContextHolder
                            .when(SecurityContextHolder::getContext)
                            .thenReturn(securityContext);

                    when(securityContext.getAuthentication())
                            .thenReturn(authentication);

                    when(authentication.getName())
                            .thenReturn(existingUser.getEmail());

                    when(userRepository.findByEmail(existingUser.getEmail()))
                            .thenReturn(Optional.of(existingUser));
                    when(spendingPeriodRepository.existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            existingUser.getUserId(),
                            LocalDate.of(2026,9,28), LocalDate.of(2026,9,20)))
                            .thenReturn(false);
                    SpendingPeriod existingSpendingPeriod =  SpendingPeriod.builder()
                            .spendingPeriodId(1L)
                            .startDate(LocalDate.of(2026,9, 20))
                            .endDate(LocalDate.of(2026, 9, 28))
                            .periodType(PeriodType.CUSTOM)
                            .budgetAmount(null)
                            .user(existingUser)
                            .build();
                    when(spendingPeriodRepository.save(any(SpendingPeriod.class)))
                            .thenReturn(existingSpendingPeriod);

                    //Act
                    SpendingPeriodResponse response = spendingPeriodService.createSpendingPeriod(request);

                    //Assert
                    assertEquals(1L, response.spendingPeriodId());
                    assertEquals(LocalDate.of(2026,9,20), response.startDate());
                    assertEquals(LocalDate.of(2026,9,28), response.endDate());
                    assertEquals(PeriodType.CUSTOM, response.periodType());
                    assertNull(response.budgetAmount());

                    //verify
                    verify(securityContext).getAuthentication();
                    verify(authentication).getName();
                    verify(userRepository).findByEmail(existingUser.getEmail());
                    verify(spendingPeriodRepository).existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            existingUser.getUserId(), LocalDate.of(2026, 9, 28),
                            LocalDate.of(2026, 9, 20));
                    verify(spendingPeriodRepository).save(any(SpendingPeriod.class));
                }
        }
    }

    @Nested
    class updateSpendingPeriodTests{

        @Test
        void shouldUpdateSpendingPeriodSuccessfully(){
            //Arrange
            SpendingPeriodUpdateRequest request = new SpendingPeriodUpdateRequest(
                    null,
                    null,
                    LocalDate.of(2026,9,20),
                    LocalDate.of(2026,9,28),
                    PeriodType.CUSTOM,
                    null

            );
            SpendingPeriod existingSpendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026,9,14))
                    .endDate( LocalDate.of(2026,9,19))
                    .periodType(PeriodType.CUSTOM)
                    .user(existingUser)
                    .budgetAmount(BigDecimal.valueOf(400))
                    .build();

            SpendingPeriod updatedSpendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 20))
                    .endDate(LocalDate.of(2026, 9, 28))
                    .periodType(PeriodType.CUSTOM)
                    .user(existingUser)
                    .budgetAmount(null)
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                        .thenReturn(Optional.of(existingUser));
                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        1L, existingUser.getUserId()))
                        .thenReturn(Optional.of(existingSpendingPeriod));
                when(spendingPeriodRepository.existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndSpendingPeriodIdNot(
                        existingUser.getUserId(), LocalDate.of(2026,9,28),
                        LocalDate.of(2026,9,20),
                        existingSpendingPeriod.getSpendingPeriodId() ))
                        .thenReturn(false);
                when(spendingPeriodRepository.save(any(SpendingPeriod.class)))
                        .thenReturn(updatedSpendingPeriod);

                //Act
                SpendingPeriodResponse response = spendingPeriodService.updateSpendingPeriod(1L, request);

                //Assert
                assertEquals(1L, response.spendingPeriodId());
                assertEquals(LocalDate.of(2026,9,20), response.startDate());
                assertEquals(LocalDate.of(2026,9,28), response.endDate());
                assertEquals(PeriodType.CUSTOM, response.periodType());
                assertNull(response.budgetAmount());

                //verify
                verify(securityContext).getAuthentication();
                verify(authentication).getName();
                verify(userRepository).findByEmail(existingUser.getEmail());
                verify(spendingPeriodRepository).findBySpendingPeriodIdAndUserUserId(
                        existingSpendingPeriod.getSpendingPeriodId(),
                        existingUser.getUserId());
                verify(spendingPeriodRepository).existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndSpendingPeriodIdNot(
                        existingUser.getUserId(), LocalDate.of(2026, 9, 28),
                        LocalDate.of(2026, 9, 20),
                        existingSpendingPeriod.getSpendingPeriodId());
                verify(spendingPeriodRepository).save(any(SpendingPeriod.class));

            }


        }


        @Test
        void shouldUpdateMonthlySpendingPeriodToCustom() {
            SpendingPeriodUpdateRequest request = new SpendingPeriodUpdateRequest(
                    null,
                    null,
                    LocalDate.of(2026, 10, 5),
                    LocalDate.of(2026, 10, 15),
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(200)
            );

            SpendingPeriod existingSpendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 1))
                    .endDate(LocalDate.of(2026, 9, 30))
                    .periodType(PeriodType.MONTHLY)
                    .budgetAmount(BigDecimal.valueOf(300))
                    .user(existingUser)
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        1L, existingUser.getUserId()))
                        .thenReturn(Optional.of(existingSpendingPeriod));

                when(spendingPeriodRepository
                        .existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndSpendingPeriodIdNot(
                                existingUser.getUserId(),
                                LocalDate.of(2026, 10, 15),
                                LocalDate.of(2026, 10, 5),
                                1L))
                        .thenReturn(false);

                when(spendingPeriodRepository.save(any(SpendingPeriod.class)))
                        .thenReturn(existingSpendingPeriod);

                SpendingPeriodResponse response =
                        spendingPeriodService.updateSpendingPeriod(1L, request);

                assertEquals(1L, response.spendingPeriodId());
                assertEquals(LocalDate.of(2026, 10, 5), response.startDate());
                assertEquals(LocalDate.of(2026, 10, 15), response.endDate());
                assertEquals(PeriodType.CUSTOM, response.periodType());
                assertEquals(BigDecimal.valueOf(200), response.budgetAmount());

                verify(spendingPeriodRepository).save(any(SpendingPeriod.class));
            }
        }

        @Test
        void shouldUpdateCustomSpendingPeriodToMonthly() {
            SpendingPeriodUpdateRequest request = new SpendingPeriodUpdateRequest(
                    10,
                    2026,
                    null,
                    null,
                    PeriodType.MONTHLY,
                    BigDecimal.valueOf(500)
            );

            SpendingPeriod existingSpendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 4))
                    .endDate(LocalDate.of(2026, 9, 11))
                    .periodType(PeriodType.CUSTOM)
                    .budgetAmount(BigDecimal.valueOf(200))
                    .user(existingUser)
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        1L, existingUser.getUserId()))
                        .thenReturn(Optional.of(existingSpendingPeriod));

                when(spendingPeriodRepository
                        .existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndSpendingPeriodIdNot(
                                existingUser.getUserId(),
                                LocalDate.of(2026, 10, 31),
                                LocalDate.of(2026, 10, 1),
                                1L))
                        .thenReturn(false);

                when(spendingPeriodRepository.save(any(SpendingPeriod.class)))
                        .thenReturn(existingSpendingPeriod);

                SpendingPeriodResponse response =
                        spendingPeriodService.updateSpendingPeriod(1L, request);

                assertEquals(1L, response.spendingPeriodId());
                assertEquals(LocalDate.of(2026, 10, 1), response.startDate());
                assertEquals(LocalDate.of(2026, 10, 31), response.endDate());
                assertEquals(PeriodType.MONTHLY, response.periodType());
                assertEquals(BigDecimal.valueOf(500), response.budgetAmount());

                verify(spendingPeriodRepository).save(any(SpendingPeriod.class));
            }
        }

        @Test
        void shouldThrowExceptionWhenUpdatingMonthlyPeriodWithMissingMonthOrYear() {
            SpendingPeriodUpdateRequest request = new SpendingPeriodUpdateRequest(
                    null,
                    2026,
                    null,
                    null,
                    PeriodType.MONTHLY,
                    BigDecimal.valueOf(200)
            );

            SpendingPeriod existingSpendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 1))
                    .endDate(LocalDate.of(2026, 9, 30))
                    .periodType(PeriodType.MONTHLY)
                    .user(existingUser)
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        1L, existingUser.getUserId()))
                        .thenReturn(Optional.of(existingSpendingPeriod));

                InvalidSpendingPeriodException exception = assertThrows(
                        InvalidSpendingPeriodException.class,
                        () -> spendingPeriodService.updateSpendingPeriod(1L, request)
                );

                assertEquals(
                        "Month and year are required for a monthly spending period",
                        exception.getMessage()
                );

                verify(spendingPeriodRepository, never()).save(any());
            }
        }

        @Test
        void shouldThrowExceptionWhenUpdatingCustomPeriodWithMissingDates() {
            SpendingPeriodUpdateRequest request = new SpendingPeriodUpdateRequest(
                    null,
                    null,
                    null,
                    null,
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(200)
            );

            SpendingPeriod existingSpendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 1))
                    .endDate(LocalDate.of(2026, 9, 30))
                    .periodType(PeriodType.MONTHLY)
                    .user(existingUser)
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        1L, existingUser.getUserId()))
                        .thenReturn(Optional.of(existingSpendingPeriod));

                InvalidSpendingPeriodException exception = assertThrows(
                        InvalidSpendingPeriodException.class,
                        () -> spendingPeriodService.updateSpendingPeriod(1L, request)
                );

                assertEquals(
                        "Start date and end date are required for a custom spending period",
                        exception.getMessage()
                );

                verify(spendingPeriodRepository, never()).save(any());
            }
        }

        @Test
        void shouldThrowExceptionWhenUpdatingSpendingPeriodToOverlappingPeriod() {
            SpendingPeriodUpdateRequest request = new SpendingPeriodUpdateRequest(
                    null,
                    null,
                    LocalDate.of(2026, 10, 5),
                    LocalDate.of(2026, 10, 15),
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(200)
            );

            SpendingPeriod existingSpendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 1))
                    .endDate(LocalDate.of(2026, 9, 30))
                    .periodType(PeriodType.MONTHLY)
                    .user(existingUser)
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        1L, existingUser.getUserId()))
                        .thenReturn(Optional.of(existingSpendingPeriod));

                when(spendingPeriodRepository
                        .existsByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndSpendingPeriodIdNot(
                                existingUser.getUserId(),
                                LocalDate.of(2026, 10, 15),
                                LocalDate.of(2026, 10, 5),
                                1L))
                        .thenReturn(true);

                SpendingPeriodOverlapException exception = assertThrows(
                        SpendingPeriodOverlapException.class,
                        () -> spendingPeriodService.updateSpendingPeriod(1L, request)
                );

                assertEquals(
                        "Spending period overlaps with an existing period",
                        exception.getMessage()
                );

                verify(spendingPeriodRepository, never()).save(any());
            }
        }

        @Test
        void shouldThrowExceptionWhenUpdatingNonexistentSpendingPeriod() {
            SpendingPeriodUpdateRequest request = new SpendingPeriodUpdateRequest(
                    null,
                    null,
                    LocalDate.of(2026, 10, 5),
                    LocalDate.of(2026, 10, 15),
                    PeriodType.CUSTOM,
                    BigDecimal.valueOf(200)
            );

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        999L, existingUser.getUserId()))
                        .thenReturn(Optional.empty());

                SpendingPeriodNotFoundException exception = assertThrows(
                        SpendingPeriodNotFoundException.class,
                        () -> spendingPeriodService.updateSpendingPeriod(999L, request)
                );

                assertEquals("Spending Period Not Found!", exception.getMessage());

                verify(spendingPeriodRepository, never()).save(any());
            }
        }

    }


    @Nested
    class getSpendingPeriodsTests {

        @Test
        void shouldGetAllSpendingPeriodsSuccessfully() {
            SpendingPeriod firstPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 1))
                    .endDate(LocalDate.of(2026, 9, 30))
                    .periodType(PeriodType.MONTHLY)
                    .budgetAmount(BigDecimal.valueOf(1000))
                    .user(existingUser)
                    .build();

            SpendingPeriod secondPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(2L)
                    .startDate(LocalDate.of(2026, 10, 5))
                    .endDate(LocalDate.of(2026, 10, 15))
                    .periodType(PeriodType.CUSTOM)
                    .budgetAmount(BigDecimal.valueOf(200))
                    .user(existingUser)
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findAllByUserUserId(
                        existingUser.getUserId()))
                        .thenReturn(List.of(firstPeriod, secondPeriod));

                List<SpendingPeriodResponse> responses =
                        spendingPeriodService.getSpendingPeriods();

                assertEquals(2, responses.size());

                assertEquals(1L, responses.get(0).spendingPeriodId());
                assertEquals(LocalDate.of(2026, 9, 1),
                        responses.get(0).startDate());
                assertEquals(PeriodType.MONTHLY, responses.get(0).periodType());
                assertEquals(BigDecimal.valueOf(1000),
                        responses.get(0).budgetAmount());

                assertEquals(2L, responses.get(1).spendingPeriodId());
                assertEquals(LocalDate.of(2026, 10, 5),
                        responses.get(1).startDate());
                assertEquals(PeriodType.CUSTOM, responses.get(1).periodType());
                assertEquals(BigDecimal.valueOf(200),
                        responses.get(1).budgetAmount());

                verify(userRepository).findByEmail(existingUser.getEmail());
                verify(spendingPeriodRepository)
                        .findAllByUserUserId(existingUser.getUserId());
            }
        }

        @Test
        void shouldReturnEmptyListWhenUserHasNoSpendingPeriods() {
            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findAllByUserUserId(
                        existingUser.getUserId()))
                        .thenReturn(List.of());

                List<SpendingPeriodResponse> responses =
                        spendingPeriodService.getSpendingPeriods();

                assertNotNull(responses);
                assertTrue(responses.isEmpty());

                verify(spendingPeriodRepository)
                        .findAllByUserUserId(existingUser.getUserId());
            }
        }
    }



    @Nested
    class getSpendingPeriodByIdTests {

        @Test
        void shouldGetSpendingPeriodByIdSuccessfully() {
            SpendingPeriod spendingPeriod = SpendingPeriod.builder()
                    .spendingPeriodId(1L)
                    .startDate(LocalDate.of(2026, 9, 1))
                    .endDate(LocalDate.of(2026, 9, 30))
                    .periodType(PeriodType.MONTHLY)
                    .budgetAmount(BigDecimal.valueOf(1000))
                    .user(existingUser)
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        1L, existingUser.getUserId()))
                        .thenReturn(Optional.of(spendingPeriod));

                SpendingPeriodResponse response =
                        spendingPeriodService.getSpendingPeriodById(1L);

                assertEquals(1L, response.spendingPeriodId());
                assertEquals(LocalDate.of(2026, 9, 1), response.startDate());
                assertEquals(LocalDate.of(2026, 9, 30), response.endDate());
                assertEquals(PeriodType.MONTHLY, response.periodType());
                assertEquals(BigDecimal.valueOf(1000), response.budgetAmount());

                verify(userRepository).findByEmail(existingUser.getEmail());
                verify(spendingPeriodRepository)
                        .findBySpendingPeriodIdAndUserUserId(
                                1L, existingUser.getUserId());
            }
        }

        @Test
        void shouldThrowExceptionWhenSpendingPeriodDoesNotExist() {
            try (MockedStatic<SecurityContextHolder> mocked =
                         mockAuthenticatedUser()) {

                when(spendingPeriodRepository.findBySpendingPeriodIdAndUserUserId(
                        999L, existingUser.getUserId()))
                        .thenReturn(Optional.empty());

                SpendingPeriodNotFoundException exception = assertThrows(
                        SpendingPeriodNotFoundException.class,
                        () -> spendingPeriodService.getSpendingPeriodById(999L)
                );

                assertEquals("Spending Period Not Found!", exception.getMessage());
            }
        }
    }


    private MockedStatic<SecurityContextHolder> mockAuthenticatedUser() {
        MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                mockStatic(SecurityContextHolder.class);

        mockedSecurityContextHolder
                .when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn(existingUser.getEmail());

        when(userRepository.findByEmail(existingUser.getEmail()))
                .thenReturn(Optional.of(existingUser));

        return mockedSecurityContextHolder;
    }
}

