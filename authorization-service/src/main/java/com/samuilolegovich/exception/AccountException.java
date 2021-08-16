package com.samuilolegovich.exception;

import com.samuilolegovich.enums.AccountStatusCode;
import com.samuilolegovich.domain.User;
import lombok.Getter;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@ResponseStatus(code = OK)
public class AccountException extends RuntimeException {
    private final AccountStatusCode accountStatusCode;
    private final String token;

    public AccountException(User user) {
        super(user.getAccountStatusCode().getMessage());
        this.accountStatusCode = user.getAccountStatusCode();
        this.token = null;
    }

    public AccountException(User user, String token) {
        super(user.getAccountStatusCode().getMessage());
        this.accountStatusCode = user.getAccountStatusCode();
        this.token = token;
    }
}
