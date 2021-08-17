package com.samuilolegovich.service.impl;

import com.samuilolegovich.domain.ChallengeQuestion;
import com.samuilolegovich.domain.User;
import com.samuilolegovich.dto.ChallengeQuestionTypeDto;
import com.samuilolegovich.dto.UserChallengeQuestionDto;
import com.samuilolegovich.enums.ChallengeQuestionType;
import com.samuilolegovich.mapper.ChallengeQuestionMapper;
import com.samuilolegovich.repository.ChallengeQuestionRepo;
import com.samuilolegovich.repository.UserRepo;
import com.samuilolegovich.service.ChallengeQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class ChallengeQuestionServiceImpl implements ChallengeQuestionService {

    private final ChallengeQuestionMapper challengeQuestionMapper;
    private final ChallengeQuestionRepo challengeQuestionRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;




    @Override
    public void saveChallengeQuestionAnswer(Long userId, UserChallengeQuestionDto userChallengeQuestionDto) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(BAD_REQUEST,
                        String.format("User with id %d not found", userId)));

        Map<ChallengeQuestionType, ChallengeQuestion> newChallengeQuestions =
                userChallengeQuestionDto.getChallengeQuestions().stream()
                .map(challengeQuestionMapper::toEntity)
                .map(challengeQuestion -> challengeQuestion.setUser(user)
                        .setAnswer(passwordEncoder.encode(challengeQuestion.getAnswer())))
                .collect(toMap(ChallengeQuestion::getType, challengeQuestion -> challengeQuestion));

        Map<ChallengeQuestionType, ChallengeQuestion> currentChallengeQuestion =
                user.getChallengeQuestions().stream().collect(toMap(ChallengeQuestion::getType, challenge -> challenge));

        newChallengeQuestions.forEach((key, value) -> {
            if (currentChallengeQuestion.containsKey(key)) {
                currentChallengeQuestion.get(key).setAnswer(value.getAnswer());
            } else {
                user.addChallengeQuestion(value);
            }
        });

        List<ChallengeQuestion> toDelete = user.getChallengeQuestions()
                .stream().filter(current -> !newChallengeQuestions.containsKey(current.getType())).collect(toList());
        toDelete.forEach(challenge -> challenge.setUser(null));
        user.getChallengeQuestions().removeAll(toDelete);
    }



    /**
     * @param email - электронная почта пользователя для поиска контрольных вопросов
     * @return случайно сгенерированный набор контрольных вопросов, объединенный с вопросами пользователя
     */
    @Override
    public List<ChallengeQuestionTypeDto> getChallengeQuestionByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(userInfo -> challengeQuestionRepo.findByUserId(userInfo.getId(), 3).stream()
                        .map(challengeQuestionMapper::toTypeDto).collect(toList())).orElse(Collections.emptyList());
    }



    /**
     * @param userId - параметр поиска
     * @return случайно сгенерированный набор контрольных вопросов, объединенный с вопросами пользователя
     */
    @Override
    public List<ChallengeQuestionTypeDto> getUserChallengeQuestion(Long userId) {
        return challengeQuestionRepo.findByUserId(userId, 3).stream()
                .map(challengeQuestionMapper::toTypeDto).collect(toList());
    }
}
