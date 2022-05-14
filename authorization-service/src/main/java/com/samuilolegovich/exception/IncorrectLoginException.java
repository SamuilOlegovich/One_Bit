package com.samuilolegovich.exception;

import lombok.Getter;

public class IncorrectLoginException extends RuntimeException {
    @Getter
    private final int incorrectAttempts;

    public IncorrectLoginException(int incorrectAttempts, String message) {
        super(message);
        this.incorrectAttempts = incorrectAttempts;
    }
}
