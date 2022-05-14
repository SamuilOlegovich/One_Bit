package com.samuilolegovich.mapper;

import com.samuilolegovich.domain.ChallengeQuestion;
import com.samuilolegovich.dto.ChallengeQuestionDto;
import com.samuilolegovich.dto.ChallengeQuestionTypeDto;
import org.springframework.stereotype.Component;

@Component
public class ChallengeQuestionMapper {

    public ChallengeQuestionTypeDto toTypeDto(ChallengeQuestion challengeQuestion) {
        return ChallengeQuestionTypeDto.builder()
                .challengeQuestionType(challengeQuestion.getType())
                .build();
    }

    public ChallengeQuestion toEntity(ChallengeQuestionDto challengeQuestionDto) {
        return ChallengeQuestion.builder()
                .type(challengeQuestionDto.getType())
                .answer(challengeQuestionDto.getAnswer())
                .build();
    }
}
