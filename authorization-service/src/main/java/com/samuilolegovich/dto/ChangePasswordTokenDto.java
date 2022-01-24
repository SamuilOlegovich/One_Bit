package com.samuilolegovich.dto;

import io.swagger.annotations.ApiModel;
import lombok.Value;

import javax.validation.constraints.NotNull;

@ApiModel(description = "Изменить старый пароль на новы.")
@Value
public class ChangePasswordTokenDto {
    @NotNull
    String token;
    @NotNull
    String oldPassword;
    @NotNull
    String newPassword;
}
