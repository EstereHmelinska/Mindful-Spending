-- V2: Add data integrity constraints

-- 1. Spending period dates must be valid
ALTER TABLE spending_period
    ADD CONSTRAINT chk_spending_period_dates
        CHECK (start_date <= end_date);

-- 2. Budget amount must be positive when provided
ALTER TABLE spending_period
    ADD CONSTRAINT chk_spending_period_budget
        CHECK (budget_amount IS NULL OR budget_amount > 0);

-- 3. Expense amount must be positive
ALTER TABLE expense
    ADD CONSTRAINT chk_expense_amount
        CHECK (amount > 0);

-- 4. Category allowance amount must be positive
ALTER TABLE category_allowance
    ADD CONSTRAINT chk_category_allowance_amount
        CHECK (amount > 0);

-- 5. Default categories must be shared
-- Custom categories must belong to a user
ALTER TABLE category
    ADD CONSTRAINT chk_category_ownership
        CHECK (
            (is_default = TRUE AND user_id IS NULL)
                OR
            (is_default = FALSE AND user_id IS NOT NULL)
            );
