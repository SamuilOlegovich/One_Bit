package com.samuilolegovich.dto;

import com.samuilolegovich.enums.ChallengeQuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@ApiModel(value = "Модель данных содержит поле типа контрольного вопроса.")
public class ChallengeQuestionTypeDto {
    @ApiModelProperty(example = "FAVORITE_SONG")
    ChallengeQuestionType challengeQuestionType;
}
