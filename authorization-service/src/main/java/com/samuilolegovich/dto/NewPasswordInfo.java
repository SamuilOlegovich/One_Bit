package com.samuilolegovich.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "Data model with with new password and challenge questions.")
public class NewPasswordInfo {
    @NotNull
    private String passwordToken;
    @NotNull
    private String newPassword;
    @Size(min = 3)
    private List<ChallengeQuestionDto> challengeQuestions;
}
