-- V1: Create initial database schema

-- 1. Users
CREATE TABLE users
(
    user_id       BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;


-- 2. Spending periods
CREATE TABLE spending_period
(
    spending_period_id BIGINT NOT NULL AUTO_INCREMENT,
    start_date         DATE   NOT NULL,
    end_date           DATE   NOT NULL,
    period_type        ENUM('CUSTOM', 'MONTHLY') NOT NULL,
    budget_amount      DECIMAL(38, 2) NULL,
    user_id            BIGINT NOT NULL,

    CONSTRAINT pk_spending_period PRIMARY KEY (spending_period_id),

    INDEX              idx_spending_period_user (user_id),

    CONSTRAINT fk_spending_period_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
) ENGINE=InnoDB;


-- 3. Categories
CREATE TABLE category
(
    category_id BIGINT       NOT NULL AUTO_INCREMENT,
    is_default  BIT(1)       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    user_id     BIGINT NULL,

    CONSTRAINT pk_category PRIMARY KEY (category_id),

    INDEX       idx_category_user (user_id),

    CONSTRAINT fk_category_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
) ENGINE=InnoDB;


-- 4. Expenses
CREATE TABLE expense
(
    expense_id         BIGINT         NOT NULL AUTO_INCREMENT,
    amount             DECIMAL(38, 2) NOT NULL,
    `date`             DATE           NOT NULL,
    description        VARCHAR(255)   NOT NULL,
    category_id        BIGINT         NOT NULL,
    spending_period_id BIGINT         NOT NULL,
    user_id            BIGINT         NOT NULL,

    CONSTRAINT pk_expense PRIMARY KEY (expense_id),

    INDEX              idx_expense_category (category_id),
    INDEX              idx_expense_spending_period (spending_period_id),
    INDEX              idx_expense_user (user_id),

    CONSTRAINT fk_expense_category
        FOREIGN KEY (category_id)
            REFERENCES category (category_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,

    CONSTRAINT fk_expense_spending_period
        FOREIGN KEY (spending_period_id)
            REFERENCES spending_period (spending_period_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,

    CONSTRAINT fk_expense_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
) ENGINE=InnoDB;


-- 5. Category allowances
CREATE TABLE category_allowance
(
    category_allowance_id BIGINT         NOT NULL AUTO_INCREMENT,
    amount                DECIMAL(38, 2) NOT NULL,
    category_id           BIGINT         NOT NULL,
    spending_period_id    BIGINT         NOT NULL,

    CONSTRAINT pk_category_allowance PRIMARY KEY (category_allowance_id),

    CONSTRAINT uk_category_allowance_category_period
        UNIQUE (category_id, spending_period_id),

    INDEX                 idx_category_allowance_spending_period (spending_period_id),

    CONSTRAINT fk_category_allowance_category
        FOREIGN KEY (category_id)
            REFERENCES category (category_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,

    CONSTRAINT fk_category_allowance_spending_period
        FOREIGN KEY (spending_period_id)
            REFERENCES spending_period (spending_period_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
) ENGINE=InnoDB;