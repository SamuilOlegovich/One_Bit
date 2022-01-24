package com.samuilolegovich.service.impl;

import com.samuilolegovich.domain.Player;
import com.samuilolegovich.dto.*;
import com.samuilolegovich.repository.PlayerRepo;
import com.samuilolegovich.service.LogginGDBService;
import lombok.AllArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.samuilolegovich.service.AccountService;

import javax.security.auth.login.AccountException;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final ConsumerTokenServices tokenService;
    private final LogginGDBService logginGDBService;
    private final TokenStore tokenStore;
    private final PlayerRepo playerRepo;

    @Override
    public AnswerEmailConfirmationDto confirmEmail(String emailToken) {
        return ;
    }

    @Override
    public AnswerToNewUserDto registerNewPlayer(NewUserDto newUserDto) {
        return ;
    }

    @Override
    public AnswerSignInDto signIn(SignInDto signInDto) {
        return ;
    }

    @Override
    public void logout(OAuth2Authentication authentication) {
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        final OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
        tokenService.revokeToken(accessToken.getValue());
        logginGDBService.logDbMessage("logout successfully", "Logout", Level.INFO);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordTokenDto changePasswordTokenDto) {
        Player player = playerRepo.findByResetPasswordToken(changePasswordTokenDto.getToken())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Неверный токен сброса пароля"));
        if (ChronoUnit.HOURS.between(player.getResetTokenTimestamp(), now()) >= resetTokenTTL) {
            throw new ResponseStatusException(BAD_REQUEST, "Cрок действия токена сброса истек");
        }
        if (isUserTempPasswordInvalid(player)) {
            player.setAccountStatusCode(TEMP_PASSWORD_INVALID);
            throw new AccountException(player);
        }

        validateUserPassword(player, passwordTokenDto.getNewPassword());
        validateIsPasswordMatch(passwordTokenDto.getOldPassword(), player.getPassword());
        activateAccount(player);
        String encodeNewPassword = passwordEncoder.encode(passwordTokenDto.getNewPassword());
        passwordChangeEvent(player, encodeNewPassword);
        logingDBService.logDbMessage("Успешно сменил пароль для собственной учетной записи", "PasswordChange", Level.INFO);
        player.setResetPasswordToken(null);
        player.setResetTokenTimestamp(null);

    }
}
