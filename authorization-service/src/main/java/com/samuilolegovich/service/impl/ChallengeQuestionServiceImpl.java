package com.samuilolegovich.service.impl;

import com.samuilolegovich.dto.ChallengeQuestionTypeDto;
import com.samuilolegovich.dto.UserChallengeQuestionDto;
import com.samuilolegovich.service.ChallengeQuestionService;

import java.util.List;

public class ChallengeQuestionServiceImpl implements ChallengeQuestionService {
    @Override
    public void saveChallengeQuestionAnswer(Long userId, UserChallengeQuestionDto userChallengeQuestionDto) {

    }

    @Override
    public List<ChallengeQuestionTypeDto> getChallengeQuestionByEmail(String email) {
        return ;
    }

    @Override
    public List<ChallengeQuestionTypeDto> getUserChallengeQuestion(Long userId) {
        return ;
    }
}
