package com.samuilolegovich.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Модель данных, используемая для вызова функции «забыл пароль». Содержит электронную почту пользователя и контрольные вопросы.")
public class ForgotPasswordDto {
    @NotNull
    private String email;
    @Size(min = 3)
    private List<ChallengeQuestionDto> challengeQuestions;
}
