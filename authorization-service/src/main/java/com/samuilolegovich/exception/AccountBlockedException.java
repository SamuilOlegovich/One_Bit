package com.samuilolegovich.exception;

import lombok.Getter;

public class AccountBlockedException extends RuntimeException {
    @Getter
    private final long minutesLeft;

    public AccountBlockedException(long minutesLeft, String message) {
        super(message);
        this.minutesLeft = minutesLeft;
    }
}
