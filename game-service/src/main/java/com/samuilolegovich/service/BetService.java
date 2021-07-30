package com.samuilolegovich.service;

import com.samuilolegovich.dto.PlayerInfoFoBetDto;
import com.samuilolegovich.enums.Enums;
import com.samuilolegovich.enums.InformationAboutRates;
import com.samuilolegovich.enums.Prize;
import com.samuilolegovich.enums.RedBlack;
import com.samuilolegovich.model.Bet;
import com.samuilolegovich.dto.WonOrNotWon;
import com.samuilolegovich.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.samuilolegovich.util.Converter.convertForUserCalculation;

@Service
public class BetService {
    @Autowired
    private ConditionRepo conditionRepo;
    @Autowired
    private DonationsRepo donationsRepo;
    @Autowired
    private ArsenalRepo arsenalRepo;
    @Autowired
    private PlayerRepo playerRepo;
    @Autowired
    private LottoRepo lottoRepo;
    @Autowired
    private Bet betClass;

    public void placeBet(PlayerInfoFoBetDto playerInfoFoBet, int bet, RedBlack redBlackBet) {

        // недопустимая ставка
        if (bet <= 0) {

        }

        // ставка выше допустимой
        if (bet > Constants.MAXIMUM_RATE) {

        }

        // если недостаточно кредитов у юзера для ставки
        if (convertForUserCalculation(playerInfoFoBet.getCredits()) < (long) bet) {

        }

        // если все хорошо делаем ставку
        WonOrNotWon wonOrNotWon = betClass.calculateTheWin(playerInfoFoBet, bet, redBlackBet);
        Enums enums = wonOrNotWon.getReplyToBet();

        // обрабатываем ответы по ставке

        // выиграл
        if (enums.equals(Prize.WIN)) {

        }

        // проиграл
        if (enums.equals(Prize.ZERO)) {

        }

        // выиграл лото 21
        if (enums.equals(Prize.LOTTO)) {

        }

        // выиграл супер лото 42
        if (enums.equals(Prize.SUPER_LOTTO)) {

        }

        // не достаточно кредитов в запасе на ответ ставке
        if (enums.equals(InformationAboutRates.NOT_ENOUGH_CREDIT_FOR_ANSWER)) {

        }
    }
}
