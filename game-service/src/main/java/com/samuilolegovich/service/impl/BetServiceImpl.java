package com.samuilolegovich.service.impl;

import com.samuilolegovich.domain.User;
import com.samuilolegovich.dto.AnswerToBetDto;
import com.samuilolegovich.dto.BetDto;
import com.samuilolegovich.dto.WonOrNotWon;
import com.samuilolegovich.enums.Enums;
import com.samuilolegovich.enums.InformationAboutRates;
import com.samuilolegovich.enums.Prize;
import com.samuilolegovich.enums.RedBlack;
import com.samuilolegovich.model.BetLogic;
import com.samuilolegovich.repository.*;
import com.samuilolegovich.service.BetService;
import com.samuilolegovich.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.samuilolegovich.enums.InformationAboutRates.*;
import static com.samuilolegovich.enums.Prize.*;
import static com.samuilolegovich.enums.RedBlack.BLACK;
import static com.samuilolegovich.enums.RedBlack.RED;
import static com.samuilolegovich.util.Converter.convertForUserCalculation;
import static com.samuilolegovich.util.LocaleHelper.getLocale;
import static com.samuilolegovich.util.ReadMessageHelper.getMessageForPlayer;

@Service
@AllArgsConstructor
public class BetServiceImpl implements BetService {
    private ConditionRepo conditionRepo;
    private DonationsRepo donationsRepo;
    private ArsenalRepo arsenalRepo;
    private LottoRepo lottoRepo;
    private BetLogic betLogic;
    private UserRepo userRepo;

    private final String YOUR_ACCOUNT_IS_NOT_ENOUGH_CREDITS_TO_BET = "there_are_not_enough_credits_in_the_account_for_a_bet";
    private final String INVALID_BET_VALUE_MAXIMUM_RATE = "invalid_bet_value_maximum_rate";
    private final String INVALID_BET_VALUE_LESS_ZERO = "invalid_bet_value_less_zero";
    private final String INVALID_BET_VALUE_ZERO = "invalid_bet_value_zero";
    private final String PLAYER_WIN_SUPER_LOTTO = "player_win_super_lotto";
    private final String NOT_CREDIT_FOR_ANSWER = "not_enough_credit_for_answer";
    private final String SOMETHING_WENT_WRONG = "something_went_wrong";
    private final String PLAYER_NOT_FOUND = "user_is_not_found";
    private final String PLAYER_WIN_LOTTO = "player_win_lotto";
    private final String PLAYER_LOST = "player_lost";
    private final String PLAYER_WIN = "player_win";
    private final String EN = "en";


    public AnswerToBetDto placeBet(BetDto betDto) {
        Optional<User> optionalPlayer = userRepo.findById(betDto.getUserId());

        return optionalPlayer.map(user ->
                placeBet(user, (RedBlack) betDto.getColorBet(), betDto.getBet()))
                .orElse(AnswerToBetDto.builder()
                        .comment(getMessageForPlayer(PLAYER_NOT_FOUND, getLocale(EN)))
                        .informationForBet(InformationAboutRates.SOMETHING_WENT_WRONG)
                        .claimedCombination(betDto.getColorBet())
                        .win(0L)
                        .build());

    }


    private AnswerToBetDto placeBet(User user, RedBlack redBlackBet, Long bet) {
        Long lottoNow = lottoRepo.findFirstByOrderByCreatedAtDesc().getTotalLottoCredits();
        Long playerCredits = user.getCredits();

        // недопустимая ставка == 0
        if (bet == 0) {
            return AnswerToBetDto.builder()
                    .comment(getMessageForPlayer(INVALID_BET_VALUE_ZERO, getLocale(EN)))
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .informationForBet(INCORRECT_RATE)
                    .claimedCombination(redBlackBet)
                    .lottoNow(lottoNow)
                    .win(0L)
                    .build();
        }

        // недопустимая ставка < 0
        if (bet < 0) {
            return AnswerToBetDto.builder()
                    .comment(getMessageForPlayer(INVALID_BET_VALUE_LESS_ZERO, getLocale(EN)))
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .informationForBet(INCORRECT_RATE)
                    .claimedCombination(redBlackBet)
                    .lottoNow(lottoNow)
                    .win(0L)
                    .build();
        }

        // ставка выше допустимой
        if (bet > Constants.MAXIMUM_RATE) {
            return AnswerToBetDto.builder()
                    .comment(getMessageForPlayer(INVALID_BET_VALUE_MAXIMUM_RATE, getLocale(EN)))
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .informationForBet(MAXIMUM_RATE)
                    .claimedCombination(redBlackBet)
                    .lottoNow(lottoNow)
                    .win(0L)
                    .build();
        }

        // если недостаточно кредитов у юзера для ставки
        if (convertForUserCalculation(playerCredits) < (long) bet) {
            return AnswerToBetDto.builder()
                    .comment(getMessageForPlayer(YOUR_ACCOUNT_IS_NOT_ENOUGH_CREDITS_TO_BET, getLocale(EN)))
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .informationForBet(INSUFFICIENT_FUNDS)
                    .claimedCombination(redBlackBet)
                    .lottoNow(lottoNow)
                    .win(0L)
                    .build();
        }

        // если все хорошо делаем ставку
        WonOrNotWon wonOrNotWon = betLogic.calculateTheWin(user, bet, redBlackBet);
        Enums enums = wonOrNotWon.getReplyToBet();

        // обрабатываем ответы по ставке

        // не достаточно кредитов в запасе на ответ ставке
        if (enums.equals(NOT_ENOUGH_CREDIT_FOR_ANSWER)) {
            return AnswerToBetDto.builder()
                    .comment(getMessageForPlayer(NOT_CREDIT_FOR_ANSWER, getLocale(EN)))
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .informationForBet(NOT_ENOUGH_CREDIT_FOR_ANSWER)
                    .claimedCombination(redBlackBet)
                    .lottoNow(lottoNow)
                    .win(0L)
                    .build();
        }

        // В методы ниже вставить код чтобы они отправляли сообщения о выигрыше в общий чат сообщений
        // в формате - никНейм выигарл - сколько

        // выиграл супер лото 42
        if (enums.equals(SUPER_LOTTO)) {
            return AnswerToBetDto.builder()
                    .comment(getMessageForPlayer(PLAYER_WIN_SUPER_LOTTO, getLocale(EN)))
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .claimedCombination(redBlackBet)
                    .winningCombination(SUPER_LOTTO)
                    .informationForBet(SUPER_LOTTO)
                    .win(wonOrNotWon.getWin())
                    .lottoNow(lottoNow)
                    .build();
        }

        // выиграл лото 21
        if (enums.equals(LOTTO)) {
            return AnswerToBetDto.builder()
                    .comment(getMessageForPlayer(PLAYER_WIN_LOTTO, getLocale(EN)))
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .claimedCombination(redBlackBet)
                    .win(wonOrNotWon.getWin())
                    .winningCombination(LOTTO)
                    .informationForBet(LOTTO)
                    .lottoNow(lottoNow)
                    .build();
        }

        // проиграл
        if (enums.equals(ZERO)) {
            return AnswerToBetDto.builder()
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .winningCombination(redBlackBet.equals(RED) ? BLACK : RED)
                    .comment(getMessageForPlayer(PLAYER_LOST, getLocale(EN)))
                    .claimedCombination(redBlackBet)
                    .informationForBet(ZERO)
                    .lottoNow(lottoNow)
                    .win(0L)
                    .build();
        }

        // выиграл
        if (enums.equals(Prize.WIN)) {
            return AnswerToBetDto.builder()
                    .totalPlayerCredits(convertForUserCalculation(playerCredits))
                    .comment(getMessageForPlayer(PLAYER_WIN, getLocale(EN)))
                    .claimedCombination(redBlackBet)
                    .winningCombination(redBlackBet)
                    .win(wonOrNotWon.getWin())
                    .informationForBet(WIN)
                    .lottoNow(lottoNow)
                    .build();
        }

        return AnswerToBetDto.builder()
                .comment(getMessageForPlayer(SOMETHING_WENT_WRONG, getLocale(EN)))
                .informationForBet(InformationAboutRates.SOMETHING_WENT_WRONG)
                .totalPlayerCredits(convertForUserCalculation(playerCredits))
                .claimedCombination(redBlackBet)
                .lottoNow(lottoNow)
                .win(0L)
                .build();
    }
}
