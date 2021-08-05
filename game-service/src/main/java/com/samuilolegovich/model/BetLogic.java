package com.samuilolegovich.model;

import com.samuilolegovich.domain.*;
import com.samuilolegovich.dto.WonOrNotWon;
import com.samuilolegovich.enums.Enums;
import com.samuilolegovich.enums.InformationAboutRates;
import com.samuilolegovich.enums.Prize;
import com.samuilolegovich.enums.RedBlack;
import com.samuilolegovich.repository.*;
import com.samuilolegovich.util.Constants;
import com.samuilolegovich.util.Converter;
import com.samuilolegovich.util.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.samuilolegovich.util.Converter.convertForUserCalculation;

@Component
@RequiredArgsConstructor
public class BetLogic {
    private final ConditionRepo conditionRepo;
    private final DonationsRepo donationsRepo;
    private final ArsenalRepo arsenalRepo;
    private final PlayerRepo playerRepo;
    private final LottoRepo lottoRepo;


    /*  Если будут проблеммы с синхронизацией и неадекватным списанием средств
        то расскоментировать код ниже. А так же внутренности класса BetConfig.

    private static volatile BetLogic bet;


    private BetLogic(ConditionRepo conditionRepo, DonationsRepo donationsRepo, ArsenalRepo arsenalRepo,
                PlayerRepo playerRepo, LottoRepo lottoRepo) {
        this.conditionRepo = conditionRepo;
        this.donationsRepo = donationsRepo;
        this.arsenalRepo = arsenalRepo;
        this.playerRepo = playerRepo;
        this.lottoRepo = lottoRepo;
    }


    public static BetLogic getInstance(ConditionRepo conditionRepo, DonationsRepo donationsRepo, ArsenalRepo arsenalRepo,
                                  PlayerRepo playerRepo, LottoRepo lottoRepo) {
        BetLogic localInstance = bet;
        if (localInstance == null) {
            synchronized (BetLogic.class) {
                localInstance = bet;
                if (localInstance == null) {
                    bet = localInstance = new BetLogic(conditionRepo, donationsRepo, arsenalRepo, playerRepo, lottoRepo);
                }
            }
        }
        return localInstance;
    }
    */




    public WonOrNotWon calculateTheWin(Player player, Long bet, RedBlack redBlackBet) {
        // получаем игрока и данные о его кредитах
        long playerCredits = player.getCredits();

        // берем последнюю запись в арсенале (она максимально актуальна на данный момент)
        Arsenal arsenal = arsenalRepo.findFirstByOrderByCreatedAtDesc();
        long arsenalCredit = arsenal.getCredits();

        // проверяем достаточно ли кредитов в запасе на ответ ставке
        if (convertForUserCalculation(arsenalCredit) <= bet) {
            return WonOrNotWon.builder()
                    .totalLottoNow(lottoRepo.findFirstByOrderByCreatedAtDesc().getLottoCredits())
                    .replyToBet(InformationAboutRates.NOT_ENOUGH_CREDIT_FOR_ANSWER)
                    .totalLoansNow(convertForUserCalculation(player.getCredits()))
                    .win(0L)
                    .build();
        }

        // Получаем состояния системы
        Lotto lotto = lottoRepo.findFirstByOrderByCreatedAtDesc();
        Condition condition = conditionRepo.findByBet(bet);

        // получаем данные по состоянию
        long lottoCredits = lotto.getLottoCredits();
        int bias = condition.getBias();

        // генерируем число
        byte generatedLotto = Generator.generate();

        // если смещение больше нуля то проверяем на выигрыш
        if (bias > Constants.ZERO_BIAS) {
            // если лото позволяет дробление
            if (checkForWinningsLotto(lottoCredits)) {
                if (generatedLotto == Constants.LOTTO)
                    return point(player, playerCredits, lottoCredits);
                if (generatedLotto == Constants.SUPER_LOTTO)
                    return superLotto(player, playerCredits, lottoCredits);
            }
            return takeIntoAccountTheBias(player, playerCredits, bet, redBlackBet, arsenalCredit,
                    lottoCredits, condition, bias);
        }

        return wonOrNotWon(player, playerCredits, bet, redBlackBet, generatedLotto,
                arsenalCredit, lottoCredits, condition);
    }





    private WonOrNotWon point(Player player, long playerCredits, long lottoCredits) {
        long onePercent = lottoCredits / 100L;
        long boobyPrize = onePercent * Constants.BOOBY_PRIZE;
        long totalDonation = donationsRepo.findFirstByOrderByCreatedAtDesc().getTotalDonations() + onePercent;

        player.setCredits(playerCredits + boobyPrize);

        lottoRepo.save(Lotto.builder()
                .lottoCredits(lottoCredits - (boobyPrize + onePercent))
                .build()
        );
        donationsRepo.save(Donations.builder()
                .totalDonations(totalDonation)
                .donations(onePercent)
                .typeWin(Prize.LOTTO)
                .build()
        );
        playerRepo.save(player);

        return WonOrNotWon.builder()
                .totalLoansNow(convertForUserCalculation(player.getCredits()))
                .win(convertForUserCalculation(boobyPrize))
                .totalLottoNow(lottoCredits)
                .replyToBet(Prize.LOTTO)
                .build();
    }



    private WonOrNotWon superLotto(Player player, long playerCredits, long lottoCredits) {
        // добавить откусывание 10 процентов в фонд
        long onePercent = lottoCredits / 100L;
        long donation = onePercent * Constants.DONATE;
        long allLotto = onePercent * Constants.PRIZE;
        long totalDonations = donationsRepo.findFirstByOrderByCreatedAtDesc().getTotalDonations() + donation;

        player.setCredits(playerCredits + allLotto);

        donationsRepo.save(Donations.builder()
                .totalDonations(totalDonations)
                .typeWin(Prize.LOTTO)
                .donations(donation)
                .build()
        );
        lottoRepo.save(Lotto.builder()
                .lottoCredits(0L)
                .build()
        );
        playerRepo.save(player);

        return WonOrNotWon.builder()
                .totalLoansNow(convertForUserCalculation(player.getCredits()))
                .win(convertForUserCalculation(allLotto))
                .replyToBet(Prize.SUPER_LOTTO)
                .totalLottoNow(lottoCredits)
                .build();
    }



    private WonOrNotWon takeIntoAccountTheBias(Player player, long playerCredits, Long bet,
                                               RedBlack redBlackBet, long arsenalCredit, long lottoCredits,
                                               Condition condition, int bias) {

        Long resultCredits = bet * Constants.FOR_USER_CALCULATIONS;

        player.setCredits(playerCredits - resultCredits);

        // перенос средств в лото или арсенал
        if (bias == Constants.ONE_BIAS) {
            lottoRepo.save(Lotto.builder()
                    .lottoCredits(lottoCredits + resultCredits)
                    .build()
            );
        } else {
            arsenalRepo.save(Arsenal.builder()
                    .credits(arsenalCredit + resultCredits)
                    .build()
            );
        }

        // уменьшаем смещение
        condition.setBias(bias - 1);

        conditionRepo.save(condition);
        playerRepo.save(player);

        return WonOrNotWon.builder()
                .totalLottoNow(lottoCredits)
                .replyToBet(Prize.ZERO)
                .win(0L)
                .build();
    }



    private WonOrNotWon wonOrNotWon(Player player, long playerCredits, Long bet, RedBlack redBlackBet,
                                    byte generatedLotto, long arsenalCredits, long lottoCredits, Condition condition) {

        // если лото позволяет дробление
        if (checkForWinningsLotto(lottoCredits)) {
            if (generatedLotto == Constants.LOTTO)
                return point(player, playerCredits, lottoCredits);
            if (generatedLotto == Constants.SUPER_LOTTO)
                return superLotto(player, playerCredits, lottoCredits);
        }

        Enums redBlackConvert = Converter.convert(generatedLotto);

        Long resultCredits = bet * Constants.FOR_USER_CALCULATIONS;

        // если игрок выиграл
        if (redBlackConvert.equals(redBlackBet)) {
            condition.setBias(Constants.BIAS);
            player.setCredits(playerCredits + resultCredits);
            arsenalRepo.save(Arsenal.builder()
                    .credits(arsenalCredits - resultCredits)
                    .build()
            );
            conditionRepo.save(condition);
            playerRepo.save(player);

            return WonOrNotWon.builder()
                    .totalLottoNow(lottoCredits)
                    .replyToBet(Prize.WIN)
                    .win(bet)
                    .build();
        }

        player.setCredits(playerCredits - resultCredits);
        lottoRepo.save(Lotto.builder()
                .lottoCredits(lottoCredits + resultCredits)
                .build()
        );
        playerRepo.save(player);

        return WonOrNotWon.builder()
                .totalLoansNow(convertForUserCalculation(player.getCredits()))
                .totalLottoNow(lottoCredits)
                .replyToBet(Prize.ZERO)
                .win(0L)
                .build();
    }



    private boolean checkForWinningsLotto(long lottoCredits) {
        return lottoCredits >= Constants.MINIMUM_LOTO_FOR_DRAWING_POSSIBILITIES;
    }
}
