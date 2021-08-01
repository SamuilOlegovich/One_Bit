package com.samuilolegovich.service;

import com.samuilolegovich.dto.AnswerToBetDto;
import com.samuilolegovich.dto.BetDto;
import com.samuilolegovich.model.Bet;

public interface BetService {
    AnswerToBetDto placeBet(BetDto bet);
}
