package com.samuilolegovich.service.impl;

import com.samuilolegovich.domain.AccountStatusCode;
import com.samuilolegovich.domain.User;
import com.samuilolegovich.dto.*;
import com.samuilolegovich.exception.AccountException;
import com.samuilolegovich.exception.IncorrectPasswordFormatException;
import com.samuilolegovich.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.passay.RuleResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import service.AccountService;
import org.slf4j.event.Level;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static com.samuilolegovich.domain.AccountStatusCode.ACTIVE_ACCOUNT;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final LoggingDBServiceImpl loggingDBService;
    private final ConsumerTokenServices tokenService;
    private final TokenStore tokenStore;
    private final UserRepo userRepo;

    @Value(("${auth.reset-token-hour-to-live}"))
    private int resetTokenTTL;



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
        loggingDBService.logDbMessage("logout successfully", "Logout", Level.INFO);
    }



    @Override
    @Transactional
    public void changePassword(ChangePasswordTokenDto changePasswordTokenDto) {
        User user = userRepo.findByResetPasswordToken(changePasswordTokenDto.getToken())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Неверный токен сброса пароля"));

        if (ChronoUnit.HOURS.between(user.getResetTokenTimestamp(), now()) >= resetTokenTTL) {
            throw new ResponseStatusException(BAD_REQUEST, "Cрок действия токена сброса истек");
        }

        if (isUserTempPasswordInvalid(user)) {
            user.setAccountStatusCode(TEMP_PASSWORD_INVALID);
            throw new AccountException(user);
        }

        validateUserPassword(user, passwordTokenDto.getNewPassword());
        validateIsPasswordMatch(passwordTokenDto.getOldPassword(), user.getPassword());
        activateAccount(user);
        String encodeNewPassword = passwordEncoder.encode(passwordTokenDto.getNewPassword());
        passwordChangeEvent(user, encodeNewPassword);
        loggingDBService.logDbMessage("Успешно сменил пароль для собственной учетной записи",
                "PasswordChange", Level.INFO);
        user.setResetPasswordToken(null);
        user.setResetTokenTimestamp(null);
    }



    ////////////////// PRIVATE //////////////////



    private boolean isUserTempPasswordInvalid(User user) {
        ArrayList<AccountStatusCode> tempPasswordStatus = newArrayList(INVITED, PASSWORD_CHANGE_REQUIRED_CODE);
        return tempPasswordStatus.contains(user.getAccountStatusCode()) && user.getPasswordTimestamp().plus(tempPasswordTTL, DAYS).isBefore(now());
    }

    private void validateUserPassword(User user, String newPassword) {
        AuthPasswordValidator passwordValidator = getValidatorForRole(user.getRoles());

        RuleResult ruleResult = passwordValidator.validate(newPassword, user.getUsername());
        if (!ruleResult.isValid()) {
            throw new IncorrectPasswordFormatException(ruleResult);
//            throw new ResponseStatusException(BAD_REQUEST, "Incorrect password format");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword()) || checkIsPasswordHistoryNotContains(user.getPasswordHistories(), newPassword)) {
            throw new ResponseStatusException(BAD_REQUEST, "Новый пароль не может быть таким же, как предыдущий.");
        }
    }

    private void validateIsPasswordMatch(String plainPassword, String encodedPassword) {
        if (!passwordEncoder.matches(plainPassword, encodedPassword))
            throw new ResponseStatusException(BAD_REQUEST, "Invalid credentials");
    }

    private void activateAccount(User user) {
        AccountStatusCode accountStatusCode = user.getAccountStatusCode();
        if (accountStatusCode.equals(PASSWORD_CHANGE_REQUIRED_CODE) || accountStatusCode.equals(PASSWORD_UPDATE_REQUIRED)) {
            user.setAccountStatusCode(ACTIVE_ACCOUNT);
        }
    }

    private void passwordChangeEvent(User user, String encodedPassword) {
        user.setPassword(encodedPassword);
        EmailInfo emailInfo = EmailInfo.builder()
                .sendTo(user.getUserName())
                .subject("Обновленный пароль")
                .text("Пароль для вашей учетной записи был недавно изменен")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, emailInfo);
    }
}
