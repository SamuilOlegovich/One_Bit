package com.samuilolegovich.controller;

import com.samuilolegovich.dto.ChallengeQuestionTypeDto;
import com.samuilolegovich.dto.UserChallengeQuestionDto;
import com.samuilolegovich.service.ChallengeQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(value = "challenge-question", tags = {"challenge-question", "question", "challenge"})
@Slf4j
@RestController
@RequestMapping("/authorization/challenge-question")
@RequiredArgsConstructor
public class ChallengeQuestionController {
    private final ChallengeQuestionService challengeQuestionService;


    @GetMapping("/email")
    @ApiOperation(value = "Получите тип контрольных вопросов по электронной почте.")
    public List<ChallengeQuestionTypeDto> getChallengeQuestionTypeByEmail(
            @ApiParam(value = "Хранит значение электронной почты пользователя.", required = true)
            @RequestParam String email) {
        return challengeQuestionService.getChallengeQuestionByEmail(email);
    }

    @GetMapping
    @ApiOperation(value = "Получить тип контрольных вопросов по userId.")
    public List<ChallengeQuestionTypeDto> getUserChallengeQuestionType(
            @ApiIgnore @AuthenticationPrincipal(expression = "userId") Long userId) {
        return challengeQuestionService.getUserChallengeQuestion(userId);
    }

    @PostMapping
    @ApiOperation(value = "Сохранить контрольные вопросы по userId.")
    public void saveChallengeQuestion(
            @ApiIgnore @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody UserChallengeQuestionDto userChallengeQuestionDto) {
        challengeQuestionService.saveChallengeQuestionAnswer(userId, userChallengeQuestionDto);
    }
}
