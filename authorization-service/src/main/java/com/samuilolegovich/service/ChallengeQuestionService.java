package com.samuilolegovich.service;

import com.samuilolegovich.dto.ChallengeQuestionTypeDto;
import com.samuilolegovich.dto.UserChallengeQuestionDto;

import java.util.List;

public interface ChallengeQuestionService {
    void saveChallengeQuestionAnswer(Long userId, UserChallengeQuestionDto userChallengeQuestionDto);

    List<ChallengeQuestionTypeDto> getChallengeQuestionByEmail(String email);

    List<ChallengeQuestionTypeDto> getUserChallengeQuestion(Long userId);
}
