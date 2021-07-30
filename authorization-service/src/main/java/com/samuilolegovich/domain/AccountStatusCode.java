package com.samuilolegovich.domain;

import lombok.Getter;

public enum AccountStatusCode {
    NEW_ACCOUNT("000", "Account is not activated"),
    ACTIVE_ACCOUNT("001", "Account is active");


    @Getter
    String code;
    @Getter
    String message;

    AccountStatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
