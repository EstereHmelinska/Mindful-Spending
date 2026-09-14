package com.mindfulspending.repository;

import com.mindfulspending.entity.CategoryAllowance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryAllowanceRepository extends JpaRepository<CategoryAllowance, Long> {

    List<CategoryAllowance> findAllBySpendingPeriodSpendingPeriodId(Long spendingPeriodId);
    Optional<CategoryAllowance> findByCategoryCategoryIdAndSpendingPeriodSpendingPeriodId(
            Long categoryId,
            Long spendingPeriodId
    );
    Optional<CategoryAllowance> findByCategoryAllowanceIdAndSpendingPeriodUserUserId(
            Long categoryAllowanceId,
            Long userId
    );
}
