package com.mindfulspending.exception;

public class SpendingPeriodNotFoundException extends RuntimeException {
    public SpendingPeriodNotFoundException() {
        super("Spending Period Not Found!");
    }
}
