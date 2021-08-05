package com.samuilolegovich.controller;

import com.samuilolegovich.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import service.AccountService;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "authorization", tags = {"authorization", "auth"})
@RestController
@RequestMapping("/authorization")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AccountService accountService;

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




}
