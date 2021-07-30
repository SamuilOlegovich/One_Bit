package com.samuilolegovich.controller;

import com.samuilolegovich.dto.NewUserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import service.AccountService;

@Api(value = "authorization", tags = {"authorization", "auth"})
@RestController
@RequestMapping("/authorization")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AccountService accountService;

    @ApiOperation(value = "Регистрируем пользователя.")
    @PostMapping("/new-user")
    public String newUserRegistration(@RequestBody NewUserDto newUserDto) {
        return "";
    }
    // TO DO
}
