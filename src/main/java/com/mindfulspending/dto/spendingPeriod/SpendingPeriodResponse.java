package com.mindfulspending.dto.spendingPeriod;

import com.mindfulspending.entity.PeriodType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
@Builder
public record SpendingPeriodResponse(
        Long spendingPeriodId,
        LocalDate startDate,
        LocalDate endDate,
        PeriodType periodType,
        BigDecimal budgetAmount
) {
}
