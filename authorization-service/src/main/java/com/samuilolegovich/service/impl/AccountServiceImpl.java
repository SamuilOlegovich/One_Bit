package com.samuilolegovich.service.impl;

import com.google.common.collect.ImmutableMap;
import com.samuilolegovich.domain.*;
import com.samuilolegovich.dto.*;
import com.samuilolegovich.enums.AccountStatusCode;
import com.samuilolegovich.enums.ChallengeQuestionType;
import com.samuilolegovich.exception.AccountBlockedException;
import com.samuilolegovich.exception.AccountException;
import com.samuilolegovich.exception.IncorrectLoginException;
import com.samuilolegovich.exception.IncorrectPasswordFormatException;
import com.samuilolegovich.repository.UserRepo;
import com.samuilolegovich.service.AccountService;
import com.samuilolegovich.service.AuthPasswordValidator;
import com.samuilolegovich.utils.UtilTokenGenerator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.passay.RuleResult;
import org.slf4j.event.Level;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.samuilolegovich.domain.EmailInfo.EMAIL_EXCHANGE;
import static com.samuilolegovich.domain.EmailInfo.EMAIL_ROUTING_KEY;
import static com.samuilolegovich.domain.Role.ROLE_ADMIN;
import static com.samuilolegovich.domain.Role.ROLE_OWNER;
import static com.samuilolegovich.enums.AccountStatusCode.*;
import static com.samuilolegovich.factory.PasswordValidatorServiceFactory.getValidatorForRole;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static org.passay.EnglishCharacterData.LowerCase;
import static org.passay.EnglishCharacterData.UpperCase;
import static org.springframework.http.HttpStatus.*;


@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final LoggingDBServiceImpl loggingDBService;
    private final ConsumerTokenServices tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final TokenEndpoint tokenEndpoint;
    private final TokenStore tokenStore;
    private final UserRepo userRepo;

    private static final String CLIENT_ID = "com-samuil-olegovich";

    @Value("${auth.path-to-email-confirmation-controller}")     //  .../authorization/confirm-email
    private final String pathToEmailConfirmationController;
    @Value(("${auth.account-block-minutes}"))
    private final int accountBlockMinutes;
    @Value("${auth.temp-password-days-to-live}")
    private final long tempPasswordTTL;
    @Value(("${auth.max-login-attempts}"))
    private final int maxLoginAttempts;
    @Value(("${auth.reset-token-hour-to-live}"))
    private final int resetTokenTTL;
    @Value("${auth.password-days-to-live}")
    private long passwordDaysToLive;
    @Value("${oauth.client.secret}")
    private String clientSecret;






    @Override
    public void registerNewPlayer(NewUserDto newUserDto) {
        // проверяем есть ли такой емейл, если есть отказываем в регистрации
        // далее создаем нового пользователя и для подтверждения отправляемего на емейл линк активации аккаунта

        Optional<User> userRepoByEmail = userRepo.findByEmail(newUserDto.getEmail());
        if (userRepoByEmail.isPresent())
            throw new ResponseStatusException(CONFLICT, "Пользователь с таким емейл уже существует.");


        Optional<User> userRepoByUserName = userRepo.findByUserName(newUserDto.getUserName());
        if (userRepoByUserName.isPresent())
            throw new ResponseStatusException(CONFLICT, "Пользователь с таким именем уже существует.");

        String tokenAuthorization = UtilTokenGenerator.generateTokenFoMailActivation();

        User user = User.builder()
                .password(passwordEncoder.encode(newUserDto.getPassword()))
                .activationAccountCode(tokenAuthorization)
                .userName(newUserDto.getUserName())
                .accountStatusCode(NEW_ACCOUNT)
                .email(newUserDto.getEmail())
                .active(false)
                .locked(false)
                .build();

        userRepo.save(user);

        String resultAuthorizationLink = pathToEmailConfirmationController
                + "/" + user.getEmail()
                + "/" + user.getActivationAccountCode();

        confirmEmail(user, resultAuthorizationLink);
        throw new ResponseStatusException(OK,
                "Вы успешно зарегистрированы! На ваш электронный адресс отправлено письмо для активации аккаунта.");
    }



    @Override
    public void confirmEmail(String userEmail, String emailToken) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден."));

        if (user.getActivationAccountCode().equals(emailToken)) {
            user.setAccountStatusCode(ACTIVE_ACCOUNT);
            user.setActivationAccountCode(null);
            userRepo.save(user);
            throw new ResponseStatusException(OK, "Добро пожаловать, " + user.getUserName() + "!");
        }
        throw new ResponseStatusException(NOT_ACCEPTABLE, "Токен активаци не совпадает.");
    }



    @Override
    public OAuth2AccessToken signIn(SignInDto signInDto) {
        User user = userRepo.findByEmail(signInDto.getLogin())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Неправильный логин или пароль."));

        handlePassword(signInDto, user);
        revokeUserAccessToken(signInDto.getLogin());
        handleBlockedAccount(user);
        validateIsPasswordChangeRequired(user);
        validatePasswordTTL(user);

        user.setIncorrectLoginCounter(0);
        user.setLastRequestTimestamp(now());
        OAuth2AccessToken oAuth2AccessToken = obtainToken(signInDto);
        user.setLastLoginTimestamp(now());
        loggingDBService.logDbMessageWithCustomUsername("login successfully ",
                "Login", user.getUserName(), Level.INFO);
        userRepo.save(user);
        return oAuth2AccessToken;
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
    public void blockAccount(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST,
                        String.format("Пользователь с таким айди %d не найден", userId)));

        if (user.getRoles().contains(ROLE_OWNER)) {
            throw new ResponseStatusException(BAD_REQUEST, "Невозможно блокировать пользователя!");
        }

        user.setAccountStatusCode(ADMIN_BLOCKED_ACCOUNT);
        userRepo.save(user);
        loggingDBService.logDbMessage("successfully blocked account of " + user.getUserName(),
                "AccountLockout", Level.INFO);
    }



    @Override
    @Transactional
    public void unblockAccount(Long userId) {
        ArrayList<AccountStatusCode> blockedStatus = newArrayList(MANUAL_BLOCKED_ACCOUNT,
                ADMIN_BLOCKED_ACCOUNT, BLOCKED_INCORRECT_ATTEMPTS);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        String.format("Пользователь с таким айди %d не найден", userId)));

        if (blockedStatus.contains(user.getAccountStatusCode())) {
            user.unblockUserAccountHelper();
            loggingDBService.logDbMessage("successfully unblocked account " + user.getUserName(),
                    "AccountLockout", Level.INFO);
        }
    }



    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userRepo.findByUserName(forgotPasswordDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Неправильный электронный адрес."));

        if (!user.getAccountStatusCode().equals(ACTIVE_ACCOUNT))
            throw new ResponseStatusException(BAD_REQUEST, "Только активная учетная запись может сбросить пароль.");

        if (!validateIsChallengeQuestionAnswersCorrect(user.getChallengeQuestions(), forgotPasswordDto.getChallengeQuestions()))
            throw new ResponseStatusException(BAD_REQUEST, "Неверный ответ на контрольный вопрос.");

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String plainPassword = passwordGenerator.generatePassword(24,
                new CharacterRule(UpperCase, 1),
                new CharacterRule(LowerCase),
                new CharacterRule(EnglishCharacterData.Digit));

        user.setAccountStatusCode(PASSWORD_CHANGE_REQUIRED_CODE);
        String encodeNewPassword = passwordEncoder.encode(plainPassword);
//        passwordChangeEvent(user, encodeNewPassword);
        loggingDBService.logDbMessageWithCustomUsername("успешно сменил пароль для своей учетной записи из-за забытого пароля",
                "PasswordChange", forgotPasswordDto.getEmail(), Level.INFO);
        user.setPassword(encodeNewPassword);
        sendPasswordToUser(user, plainPassword);

    }



    @Override
    @Transactional
    public void resetPassword(NewPasswordInfo newPasswordInfo) {
        User user = userRepo.findByResetPasswordToken(newPasswordInfo.getPasswordToken())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Неверный токен сброса пароля"));

        if (ChronoUnit.HOURS.between(user.getResetTokenTimestamp(), now()) >= resetTokenTTL) {
            throw new ResponseStatusException(BAD_REQUEST, "срок действия токена сброса истек");
        }

        if (!validateIsChallengeQuestionAnswersCorrect(user.getChallengeQuestions(), newPasswordInfo.getChallengeQuestions()))
            throw new ResponseStatusException(BAD_REQUEST, "Неверный ответ на контрольный вопрос");

        validateUserPassword(user, newPasswordInfo.getNewPassword());
        activateAccount(user);
        String encodeNewPassword = passwordEncoder.encode(newPasswordInfo.getNewPassword());
        passwordChangeEvent(user, encodeNewPassword);
        loggingDBService.logDbMessage("успешно сбросить пароль для собственной учетной записи",
                "PasswordReset", Level.INFO);
        user.setResetPasswordToken(null);
        user.setResetTokenTimestamp(null);
    }

    @Override
    @Transactional
    public void blockAccount(Long userId, Long daysToBlock) {
        User user = userRepo.findById(userId)
                .orElseThrow(() ->  new ResponseStatusException(BAD_REQUEST, "Неверный ответ на контрольный вопрос"));

        if (!user.getAccountStatusCode().equals(ACTIVE_ACCOUNT)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Невозможно заблокировать аккаунт, который не находится в состоянии ACTIVE_ACCOUNT");
        }

        user.setAccountStatusCode(MANUAL_BLOCKED_ACCOUNT);
        user.setDaysToBlock(daysToBlock);
        user.setAccountBlockTimestamp(now());

        loggingDBService.logDbMessage("успешно заблокировал свой аккаунт",
                "AccountLockout", Level.INFO);
        revokeUserAccessToken(user.getUserName());
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

        validateUserPassword(user, changePasswordTokenDto.getNewPassword());
        validateIsPasswordMatch(changePasswordTokenDto.getOldPassword(), user.getPassword());
        activateAccount(user);
        String encodeNewPassword = passwordEncoder.encode(changePasswordTokenDto.getNewPassword());
        passwordChangeEvent(user, encodeNewPassword);
        loggingDBService.logDbMessage("Успешно сменил пароль для собственной учетной записи",
                "PasswordChange", Level.INFO);
        user.setResetPasswordToken(null);
        user.setResetTokenTimestamp(null);
    }




    ////////////////// PRIVATE //////////////////





    private void handlePassword(SignInDto signInDto, User user) {
        // сверяем пароли и следим за тем чтобы пароль не перебирался больше чем укаано раз
        if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            final int counter = user.getIncorrectLoginCounter();
            if (counter < maxLoginAttempts) {
                user.setIncorrectLoginCounter(counter + 1);
                user.setLastLoginTimestamp(now());
                userRepo.save(user);
                loggingDBService.logDbMessage("login failed(wrong password) " + user.getUserName(),
                        "AccessDenied", Level.WARN);
                throw new IncorrectLoginException(counter + 1, INVALID_CREDENTIALS.getMessage());
            } else {
                user.setAccountStatusCode(BLOCKED_INCORRECT_ATTEMPTS);
                user.setAccountBlockTimestamp(now());
                user.setIncorrectLoginCounter(0);
                userRepo.save(user);
                throw new AccountException(user);
            }
        }
    }



    private void revokeUserAccessToken(String username) {
        Collection<OAuth2AccessToken> userToken = tokenStore.findTokensByClientIdAndUserName(CLIENT_ID, username);
        if (!userToken.isEmpty()) {
            userToken.forEach(t -> tokenService.revokeToken(t.getValue()));
        }
    }



    private void handleBlockedAccount(User user) {
        switch (user.getAccountStatusCode()) {
            case BLOCKED_INCORRECT_ATTEMPTS:
                long minutesPassed = ChronoUnit.MINUTES.between(user.getAccountBlockTimestamp(), now());
                if (minutesPassed >= accountBlockMinutes) {
                    user.unblockUserAccountHelper();
                    break;
                } else {
                    throw new AccountBlockedException(accountBlockMinutes - minutesPassed, BLOCKED_INCORRECT_ATTEMPTS.getMessage());
                }
            case MANUAL_BLOCKED_ACCOUNT:
                if (now().isAfter(user.getAccountBlockTimestamp().plus(user.getDaysToBlock(), DAYS))) {
                    user.unblockUserAccountHelper();
                    break;
                } else
                    throw new AccountException(user);
            case NEW_ACCOUNT:
                throw new AccountException(user);
            default:
                break;
        }

    }



    private void validateIsPasswordChangeRequired(User user) {
        String token = UUID.randomUUID().toString();
        if (user.getAccountStatusCode().equals(PASSWORD_CHANGE_REQUIRED_CODE)) {
            user.setResetPasswordToken(token);
            user.setResetTokenTimestamp(now());
            userRepo.save(user);
            throw new AccountException(user, token);
        }
    }



    private void validatePasswordTTL(User user) {
        String token = UUID.randomUUID().toString();
        if (!isAdmin(user)
                && (user.getAccountStatusCode().equals(PASSWORD_UPDATE_REQUIRED)
                || DAYS.between(user.getPasswordTimestamp(), now()) >= passwordDaysToLive)) {
            user.setAccountStatusCode(PASSWORD_UPDATE_REQUIRED);
            user.setResetPasswordToken(token);
            user.setResetTokenTimestamp(now());
            userRepo.save(user);
            throw new AccountException(user, token);
        }
    }



    private boolean isAdmin(User userInfo) {
        Role role = userInfo.getRoles().stream().findFirst().get();
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_OWNER);
    }



    @SneakyThrows
    private OAuth2AccessToken obtainToken(SignInDto signInDto) {
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(CLIENT_ID, clientSecret, emptyList());
        return tokenEndpoint.postAccessToken(credentials, oauthParams(signInDto.getLogin(), signInDto.getPassword())).getBody();
    }



    private Map<String, String> oauthParams(String userName, String password) {
        return ImmutableMap.of(
                "username", userName,
                "password", password,
                "grant_type", "password",
                "scope", "read, write");
    }



    private boolean isUserTempPasswordInvalid(User user) {
        ArrayList<AccountStatusCode> tempPasswordStatus = newArrayList(INVITED, PASSWORD_CHANGE_REQUIRED_CODE);
        return tempPasswordStatus.contains(user.getAccountStatusCode())
                && user.getPasswordTimestamp().plus(tempPasswordTTL, DAYS).isBefore(now());
    }



    private void validateUserPassword(User user, String newPassword) {
        AuthPasswordValidator passwordValidator = getValidatorForRole(user.getRoles());

        RuleResult ruleResult = passwordValidator.validate(newPassword, user.getUserName());
        if (!ruleResult.isValid()) throw new IncorrectPasswordFormatException(ruleResult);
//            throw new ResponseStatusException(BAD_REQUEST, "Incorrect password format");

        if (passwordEncoder.matches(newPassword, user.getPassword())
                || checkIsPasswordHistoryNotContains(user.getPasswordHistories(), newPassword))
            throw new ResponseStatusException(BAD_REQUEST, "Новый пароль не может быть таким же, как предыдущий.");
    }



    // проверяем не было ли уже таких паролей
    private boolean checkIsPasswordHistoryNotContains(List<PasswordHistory> passwordsHistory, String password) {
        return passwordsHistory.stream()
                .anyMatch(encryptedPass -> passwordEncoder.matches(password, encryptedPass.getPassword()));
    }



    // сверяем пароли - открытый и закодированный
    private void validateIsPasswordMatch(String plainPassword, String encodedPassword) {
        if (!passwordEncoder.matches(plainPassword, encodedPassword))
            throw new ResponseStatusException(BAD_REQUEST, "Invalid credentials");
    }



    private void activateAccount(User user) {
        AccountStatusCode accountStatusCode = user.getAccountStatusCode();
        if (accountStatusCode.equals(PASSWORD_CHANGE_REQUIRED_CODE)
                || accountStatusCode.equals(PASSWORD_UPDATE_REQUIRED)) {
            user.setAccountStatusCode(ACTIVE_ACCOUNT);
        }
    }



    // формируем письмо и кидаем в очередь на отсылку
    private void passwordChangeEvent(User user, String encodedPassword) {
        user.setPassword(encodedPassword);
        EmailInfo emailInfo = EmailInfo.builder()
                .sendTo(user.getUserName())
                .subject("Обновленный пароль")
                .text("Пароль для вашей учетной записи был недавно изменен")
                .build();

        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, emailInfo);
    }



    private void confirmEmail(User user, String authorizationLink) {
        EmailInfo emailInfo = EmailInfo.builder()
                .sendTo(user.getEmail())
                .subject("Подтвердить емейл")
                .text("Для подтверждения перейдите по ссылке - " + authorizationLink)
                .build();

        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, emailInfo);

    }



    private boolean validateIsChallengeQuestionAnswersCorrect(List<ChallengeQuestion> userAnswer, List<ChallengeQuestionDto> providedAnswers) {

        Map<ChallengeQuestionType, String> challengeAnswers = userAnswer.stream()
                .collect(toMap(ChallengeQuestion::getType,
                        ChallengeQuestion::getAnswer));

        Map<ChallengeQuestionType, String> providedChallengeQuestions = providedAnswers.stream()
                .collect(toMap(ChallengeQuestionDto::getType, ChallengeQuestionDto::getAnswer));

        return challengeAnswers.entrySet().stream()
                .filter(challenge -> providedChallengeQuestions.containsKey(challenge.getKey()))
                .allMatch(challenge -> passwordEncoder.matches(providedChallengeQuestions.get(challenge.getKey()), challenge.getValue()));
    }


    private void sendPasswordToUser(User user, String plainPassword) {
        EmailInfo emailInfo = EmailInfo.builder()
                .sendTo(user.getEmail())
                .subject("Смена пароля")
                .text(String.format("Новый сгенерированный пароль: %s", plainPassword))
                .build();

        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, emailInfo);
    }
}
