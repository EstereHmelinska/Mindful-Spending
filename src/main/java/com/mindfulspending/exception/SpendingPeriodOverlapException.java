package com.mindfulspending.exception;

public class SpendingPeriodOverlapException extends RuntimeException {
    public SpendingPeriodOverlapException(String message) {
        super(message);
    }
}
