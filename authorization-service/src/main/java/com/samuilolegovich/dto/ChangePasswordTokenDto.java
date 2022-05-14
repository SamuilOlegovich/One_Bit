package com.samuilolegovich.dto;

import io.swagger.annotations.ApiModel;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@ApiModel(description = "Изменить старый пароль на новы.")
public class ChangePasswordTokenDto {
    @NotNull
    String token;
    @NotNull
    String oldPassword;
    @NotNull
    String newPassword;
}
