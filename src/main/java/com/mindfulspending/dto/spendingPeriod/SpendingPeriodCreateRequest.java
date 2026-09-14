package com.mindfulspending.dto.spendingPeriod;


import com.mindfulspending.entity.PeriodType;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SpendingPeriodCreateRequest(
        Integer month,
        Integer year,
        LocalDate startDate,
        LocalDate endDate,
        PeriodType periodType,
        @Positive(message="Amount must be greater than 0!")
        BigDecimal budgetAmount
) {
}
