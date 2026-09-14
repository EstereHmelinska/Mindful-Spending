package com.mindfulspending.service;

import com.mindfulspending.dto.spendingPeriod.SpendingPeriodCreateRequest;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodResponse;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodUpdateRequest;
import java.util.List;

public interface SpendingPeriodService {

    SpendingPeriodResponse createSpendingPeriod(SpendingPeriodCreateRequest request);
    SpendingPeriodResponse updateSpendingPeriod(Long spendingPeriodId, SpendingPeriodUpdateRequest request);
    List<SpendingPeriodResponse> getSpendingPeriods();
    SpendingPeriodResponse getSpendingPeriodById(Long spendingPeriodId);
}
