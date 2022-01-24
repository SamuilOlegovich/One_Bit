package com.samuilolegovich.exception;

import com.samuilolegovich.domain.AccountStatusCode;
import com.samuilolegovich.domain.Player;
import lombok.Getter;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@ResponseStatus(code = OK)
public class AccountException extends RuntimeException {

    private final AccountStatusCode accountStatusCode;
    private final String token;

    public AccountException(Player player) {
        super(player.getAccountStatusCode().getMessage());
        this.accountStatusCode = player.getAccountStatusCode();
        this.token = null;
    }

    public AccountException(Player player, String token) {
        super(player.getAccountStatusCode().getMessage());
        this.accountStatusCode = player.getAccountStatusCode();
        this.token = token;
    }

}
