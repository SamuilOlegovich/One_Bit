package com.samuilolegovich.dto;

import com.samuilolegovich.enums.ChallengeQuestionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ChallengeQuestionDto {
    @NotNull
    private ChallengeQuestionType type;
    @NotEmpty
    private String answer;
}
