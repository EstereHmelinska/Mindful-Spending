package com.mindfulspending.repository;

import com.mindfulspending.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByUserUserId(Long userId);
    List<Expense> findAllBySpendingPeriodSpendingPeriodId(Long spendingPeriodId);
    List<Expense> findAllByCategoryCategoryIdAndSpendingPeriodSpendingPeriodId(
            Long categoryId,
            Long spendingPeriodId
    );
    Optional<Expense> findByExpenseIdAndUserUserId(Long expenseId, Long userId);

}
