package com.samuilolegovich.controller;

import com.samuilolegovich.domain.security.UserDetailsImpl;
import com.samuilolegovich.dto.*;
import com.samuilolegovich.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(value = "authorization", tags = {"authorization", "auth"})
@RestController
@RequestMapping("/authorization")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AccountService accountService;




    @GetMapping("/user")
    @ApiOperation(value = "Получить принципала пользователя. Создать модель PlayerDto из модели данных аутентификации.")
    public Object principal(@AuthenticationPrincipal Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return UserDto.builder()
                .playerId(principal.getUserId())
                .userName(principal.getUsername())
                .roles(principal.getRoles())
                .build();
    }



    @PostMapping("/new-user")
    @ApiOperation(value = "Регистрируем пользователя.")
    public void newUserRegistration(@RequestBody NewUserDto newUserDto) {
        accountService.registerNewPlayer(newUserDto);
    }



    @PostMapping("/confirm-email/{userId}/{emailToken}")
    @ApiOperation(value = "Подтверждаем емейл по токену")
    private void confirmEmail(@PathVariable String userEmail,
                              @PathVariable String emailToken) {
        accountService.confirmEmail(userEmail, emailToken);
    }



    @PostMapping("/log-in")
    @ApiOperation(value = "Вход")
    private OAuth2AccessToken signIn(@RequestBody SignInDto signInDto) {
        return accountService.signIn(signInDto);
    }



    @PostMapping("/logout")
    @ApiOperation(value = "Выход")
    public void logout(@ApiIgnore OAuth2Authentication auth) {
        accountService.logout(auth);
    }



    // разобраться как оно работает и допилить
    @PostMapping("/account/{userId}/block")
    @ApiOperation(value = "Заблокировать учетную запись пользователя.")
    public void blockUserAccount(
            @ApiParam(value = "Хранит значение идентификатора пользователя.", required = true)
            @PathVariable Long userId) {
        accountService.blockAccount(userId);
    }



    @PostMapping("/my-account/block")
    @ApiOperation(value = "Блокировать учетную запись пользователя по желанию пользователя.")
    public void blockMyAccount(@ApiIgnore @AuthenticationPrincipal(expression = "userId") Long userId,
                               @RequestBody @Valid MyAccountBlockDto myAccountBlockDto) {
        accountService.blockAccount(userId, myAccountBlockDto.getDaysToBlock());
    }



    @PostMapping("/account/{userId}/unblock")
    @ApiOperation(value = "Разблокировать учетную запись пользователя.")
    public void unblockAccount(
            @ApiParam(value = "Хранит значение идентификатора пользователя.", required = true)
            @PathVariable Long userId) {
        accountService.unblockAccount(userId);
    }



    // разобраться как это работает и напилить реализацию
    @PostMapping("/password/forgot")
    @ApiOperation(value = "Сброс забытого пароля.")
    public void forgotPassword(@RequestBody @Valid ForgotPasswordDto forgotPasswordDto) {
        accountService.forgotPassword(forgotPasswordDto);
    }



    @PostMapping("/password/reset")
    @ApiOperation(value = "Функция сброса забытого пароля.")
    public void resetPassword(@Valid @RequestBody NewPasswordInfo newPasswordInfo) {
        accountService.resetPassword(newPasswordInfo);
    }



    @PostMapping("/password/change-by-token")
    @ApiOperation(value = "Сменить пароль по токену")
    public void changePasswordByToken(@Valid @RequestBody ChangePasswordTokenDto changePasswordTokenDto) {
        accountService.changePassword(changePasswordTokenDto);
    }

}
