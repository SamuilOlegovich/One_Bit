package com.samuilolegovich.dto;

import com.samuilolegovich.annotation.ChallengeQuestionConstraint;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Collection;

@Data
@NoArgsConstructor
@ChallengeQuestionConstraint
@ApiModel(value = "Модель данных содержит не менее 5 контрольных вопросов.")
public class UserChallengeQuestionDto {
    @Size(min = 5)
    private Collection<ChallengeQuestionDto> challengeQuestions;
}
