package com.mindfulspending.exception.spendingPeriod;

public class SpendingPeriodNotFoundException extends RuntimeException {
    public SpendingPeriodNotFoundException() {
        super("Spending Period Not Found!");
    }
}
