package com.mindfulspending.service.impl;

import com.mindfulspending.dto.spendingPeriod.SpendingPeriodCreateRequest;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodResponse;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodUpdateRequest;
import com.mindfulspending.entity.PeriodType;
import com.mindfulspending.entity.SpendingPeriod;
import com.mindfulspending.entity.User;
import com.mindfulspending.exception.InvalidSpendingPeriodException;
import com.mindfulspending.exception.SpendingPeriodOverlapException;
import com.mindfulspending.exception.UserNotFoundException;
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
        void shouldUpdateMonthlySpendingPeriodToCustom(){

        }

        @Test
        void shouldUpdateCustomSpendingPeriodToMonthly(){

        }

        @Test
        void shouldThrowExceptionWhenUpdatingMonthlyPeriodWithMissingMonthOrYear(){

        }

        @Test
        void shouldThrowExceptionWhenUpdatingCustomPeriodWithMissingDates(){

        }

        @Test
        void shouldThrowExceptionWhenUpdatingSpendingPeriodToOverlappingPeriod(){

        }

        @Test
        void shouldThrowExceptionWhenUpdatingNonexistentSpendingPeriod(){

        }
    }

    @Nested
    class getSpendingPeriodsTests{

    }

    @Nested
    class getSpendingPeriodByIdTests{

    }
}