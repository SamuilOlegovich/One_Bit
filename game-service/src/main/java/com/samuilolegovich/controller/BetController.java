package com.samuilolegovich.controller;

import com.samuilolegovich.dto.AnswerToBetDto;
import com.samuilolegovich.dto.BetDto;
import com.samuilolegovich.service.BetService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "BetController", tags = {"BetController", "Bet"})
@Slf4j
@RestController
@RequestMapping("/place-bet-on")
@RequiredArgsConstructor
public class BetController {
    private BetService betService;

    @PostMapping("/red-or-black")
    public AnswerToBetDto placeBetOnBlackOrRed(@RequestBody BetDto bet) {
        return betService.placeBet(bet);
    }
}
