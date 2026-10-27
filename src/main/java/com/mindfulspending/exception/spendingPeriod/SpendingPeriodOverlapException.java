package com.mindfulspending.exception.spendingPeriod;

public class SpendingPeriodOverlapException extends RuntimeException {
    public SpendingPeriodOverlapException(String message) {
        super(message);
    }
}
