package com.samuilolegovich.controller;

import com.samuilolegovich.dto.AnswerToBetDto;
import com.samuilolegovich.dto.BetDto;
import com.samuilolegovich.service.BetService;
import com.samuilolegovich.service.DataService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api(value = "BetController", tags = {"BetController", "BetLogic"})
@Slf4j
@RestController
@RequestMapping("/place-bet-on")
@RequiredArgsConstructor
public class BetController {
    private DataService dataService;
    private BetService betService;

    @GetMapping()
    public AnswerToBetDto getDataFoThisPage() {
        return dataService.getDataFoThisPage();
    }

    @PostMapping("/red-or-black")
    public AnswerToBetDto placeBetOnBlackOrRed(@RequestBody BetDto bet) {
        return betService.placeBet(bet);
    }
}
