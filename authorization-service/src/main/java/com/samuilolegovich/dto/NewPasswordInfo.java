package com.samuilolegovich.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "Модель данных с новым паролем и контрольными вопросами.")
public class NewPasswordInfo {
    @NotNull
    private String passwordToken;
    @NotNull
    private String newPassword;
    @Size(min = 3)
    private List<ChallengeQuestionDto> challengeQuestions;
}
