package com.samuilolegovich.controller;

import com.samuilolegovich.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import com.samuilolegovich.service.AccountService;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(value = "authorization", tags = {"authorization", "auth"})
@RestController
@RequestMapping("/authorization")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AccountService accountService;

    @ApiOperation(value = "Получить принципала пользователя. " +
            "Создать модель PlayerDto из модели данных аутентификации.")
    @GetMapping("/user")
    public Object principal(@AuthenticationPrincipal Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        return PlayerDto.builder()
                .playerId(principal.getUserId())
                .userName(principal.getUsername())
                .roles(principal.getRoles())
                .build();
    }



    @ApiOperation(value = "Регистрируем пользователя.")
    @PostMapping("/new-user")
    public AnswerToNewUserDto newUserRegistration(@RequestBody NewUserDto newUserDto) {
        return accountService.registerNewPlayer(newUserDto);
    }



    @ApiOperation(value = "Подтверждаем емейл по токену")
    @PostMapping("/email/{emailToken}")
    private AnswerEmailConfirmationDto confirmEmail(@PathVariable String emailToken) {
        return accountService.confirmEmail(emailToken);
    }



    @ApiOperation(value = "Вход")
    @PostMapping("/log-in")
    private AnswerSignInDto signIn(@RequestBody SignInDto signInDto) {
        return accountService.signIn(signInDto);
    }



    @ApiOperation(value = "Выход")
    @PostMapping("/logout")
    public void logout(@ApiIgnore OAuth2Authentication auth) {
        accountService.logout(auth);
    }



    @ApiOperation(value = "Сменить пароль по токену")
    @PostMapping("/password/change-by-token")
    public void changePasswordByToken(@Valid @RequestBody ChangePasswordTokenDto changePasswordTokenDto) {
        accountService.changePassword(changePasswordTokenDto);
    }



    @ApiOperation(value = "Заблокировать учетную запись пользователя.")
    @PostMapping("/account/{userId}/block")
    public void blockUserAccount(
            @ApiParam(value = "Хранит значение идентификатора пользователя.", required = true)
            @PathVariable Long userId) {
        accountService.blockAccount(userId);
    }



    @ApiOperation(value = "Разблокировать учетную запись пользователя.")
    @PostMapping("/account/{userId}/unblock")
    public void unblockAccount(
            @ApiParam(value = "Хранит значение идентификатора пользователя.", required = true)
            @PathVariable Long userId) {
        accountService.unblockAccount(userId);

    }



    @ApiOperation(value = "Блокировать учетную запись пользователя по желанию пользователя.")
    @PostMapping("/my-account/block")
    public void blockMyAccount(
            @ApiIgnore @AuthenticationPrincipal(expression = "userId") Long userId,
            @RequestBody @Valid MyAccountBlockDto myAccountBlockDto) {

        accountService.blockAccount(userId, myAccountBlockDto.getDaysToBlock());
    }



    // разобраться как это работает и напилить реализацию
    @ApiOperation(value = "Сброс забытого пароля.")
    @PostMapping("/password/forgot")
    public void forgotPassword(@RequestBody @Valid ForgotPasswordDto forgotPasswordDto) {
        accountService.forgotPassword(forgotPasswordDto);
    }


    @ApiOperation(value = "Функция сброса забытого пароля.")
    @PostMapping("/password/reset")
    public void resetPassword(@Valid @RequestBody NewPasswordInfo newPasswordInfo) {
        accountService.resetPassword(newPasswordInfo);
    }

    @ApiOperation(value = "Change password by token.")
    @PostMapping("/password/change-by-token")
    public void changePasswordByToken(@Valid @RequestBody ChangePasswordTokenDto changePasswordTokenDto) {
        accountService.changePassword(changePasswordTokenDto);
    }






}
