package com.samuilolegovich.validation;

import com.samuilolegovich.annotation.ChallengeQuestionConstraint;
import com.samuilolegovich.dto.ChallengeQuestionDto;
import com.samuilolegovich.dto.UserChallengeQuestionDto;
import com.samuilolegovich.enums.ChallengeQuestionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChallengeQuestionValidation implements ConstraintValidator<ChallengeQuestionConstraint, UserChallengeQuestionDto> {
    @Value("${auth.min-challenge-question}")
    private int minChallengeQuestion;

    public void initialize(ChallengeQuestionConstraint constraint) {
    }

    public boolean isValid(UserChallengeQuestionDto questions, ConstraintValidatorContext context) {

        Map<ChallengeQuestionType, List<ChallengeQuestionDto>> groupByChallengeType = questions
                .getChallengeQuestions().stream()
                .collect(Collectors.groupingBy(ChallengeQuestionDto::getType));

        return groupByChallengeType.entrySet().stream()
                .allMatch(entry -> entry.getValue().size() == 1) && groupByChallengeType.size() >= minChallengeQuestion;
    }
}
