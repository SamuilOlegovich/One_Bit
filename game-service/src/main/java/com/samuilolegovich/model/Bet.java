package com.samuilolegovich.model;

import com.samuilolegovich.domain.Arsenal;
import com.samuilolegovich.domain.Lotto;
import com.samuilolegovich.dto.PlayerInfoFoBetDto;
import com.samuilolegovich.dto.WonOrNotWon;
import com.samuilolegovich.enums.Enums;
import com.samuilolegovich.enums.InformationAboutRates;
import com.samuilolegovich.enums.Prize;
import com.samuilolegovich.enums.RedBlack;
import com.samuilolegovich.util.Constants;
import com.samuilolegovich.util.Converter;
import com.samuilolegovich.util.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bet {
    private final ConditionRepo conditionRepo;
    private final DonationsRepo donationsRepo;
    private final ArsenalRepo arsenalRepo;
    private final PlayerRepo playerRepo;
    private final LottoRepo lottoRepo;


    /*  Если будут проблеммы с синхронизацией и неадекватным списанием средств
        то расскоментировать код ниже. А так же внутренности класса BetConfig.

    private static volatile Bet bet;


    private Bet(ConditionRepo conditionRepo, DonationsRepo donationsRepo, ArsenalRepo arsenalRepo,
                PlayerRepo playerRepo, LottoRepo lottoRepo) {
        this.conditionRepo = conditionRepo;
        this.donationsRepo = donationsRepo;
        this.arsenalRepo = arsenalRepo;
        this.playerRepo = playerRepo;
        this.lottoRepo = lottoRepo;
    }


    public static Bet getInstance(ConditionRepo conditionRepo, DonationsRepo donationsRepo, ArsenalRepo arsenalRepo,
                                  PlayerRepo playerRepo, LottoRepo lottoRepo) {
        Bet localInstance = bet;
        if (localInstance == null) {
            synchronized (Bet.class) {
                localInstance = bet;
                if (localInstance == null) {
                    bet = localInstance = new Bet(conditionRepo, donationsRepo, arsenalRepo, playerRepo, lottoRepo);
                }
            }
        }
        return localInstance;
    }
    */




    public WonOrNotWon calculateTheWin(PlayerInfoFoBetDto playerInfoFoBet, int bet, RedBlack redBlackBet) {
        // получаем игрока и данные о его кредитах
        long playerCredits = playerInfoFoBet.getCredits();

        // берем последнюю запись в арсенале (она максимально актуальна на данный момент)
        Arsenal arsenal = arsenalRepo.findFirstByOrderByCreatedAtDesc();
        long arsenalCredit = arsenal.getCredits();

        // проверяем достаточно ли кредитов в запасе на ответ ставке
        if (Converter.convertForUserCalculation(arsenalCredit) <= (long) bet)
            return WonOrNotWon.builder()
                    .replyToBet(InformationAboutRates.NOT_ENOUGH_CREDIT_FOR_ANSWER)
                    .win(0).build();

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
                if (generatedLotto == Constants.LOTTO) return point(playerInfoFoBet, playerCredits, lottoCredits);
                if (generatedLotto == Constants.SUPER_LOTTO) return superLotto(playerInfoFoBet, playerCredits, lottoCredits);
            }
            return takeIntoAccountTheBias(playerInfoFoBet, playerCredits, bet, redBlackBet, arsenalCredit,
                    lottoCredits, condition, bias);
        }

        return wonOrNotWon(playerInfoFoBet, playerCredits, bet, redBlackBet, generatedLotto,
                arsenalCredit, lottoCredits, condition);
    }





    private WonOrNotWon point(Player player, long playerCredits, long lottoCredits) {
        long onePercent = lottoCredits / 100;
        long boobyPrize = onePercent * Constants.BOOBY_PRIZE;
        long totalDonation = donationsRepo.findFirstByOrderByCreatedAtDesc().getTotalDonations() + onePercent;

        player.setCredits(playerCredits + boobyPrize);

        lottoRepo.save(new Lotto(lottoCredits - (boobyPrize + onePercent)));
        donationsRepo.save(new Donations(onePercent, totalDonation, Prize.LOTTO));
        playerRepo.save(player);

        return new Win(Prize.LOTTO, boobyPrize / Constants.FOR_USER_CALCULATIONS);
    }



    private WonOrNotWon superLotto(Player player, long playerCredits, long lottoCredits) {
        // добавить откусывание 10 процентов в фонд
        long onePercent = lottoCredits / 100;
        long donation = onePercent * Constants.DONATE;
        long allLotto = onePercent * Constants.PRIZE;
        long totalDonations = donationsRepo.findFirstByOrderByCreatedAtDesc().getTotalDonations() + donation;

        player.setCredits(playerCredits + allLotto);

        donationsRepo.save(new Donations(donation, totalDonations, Prize.LOTTO));
        lottoRepo.save(new Lotto(0));
        playerRepo.save(player);

        return new Win(Prize.LOTTO, allLotto / Constants.FOR_USER_CALCULATIONS);
    }



    private WonOrNotWon takeIntoAccountTheBias(Player player, long playerCredits, int bet, RedBlack redBlackBet,
                                       long arsenalCredit, long lottoCredits, Condition condition, int bias) {

        int resultCredits = bet * Constants.FOR_USER_CALCULATIONS;

        player.setCredits(playerCredits - resultCredits);

        // перенос средств в лото или арсенал
        if (bias == Constants.ONE_BIAS) {
            lottoRepo.save(new Lotto(lottoCredits + resultCredits));
        } else {
            arsenalRepo.save(new Arsenal(arsenalCredit + resultCredits));
        }

        // уменьшаем смещение
        condition.setBias(bias - 1);

        conditionRepo.save(condition);
        playerRepo.save(player);

        return new Win(Prize.ZERO, 0);
    }



    private WonOrNotWon wonOrNotWon(Player player, long playerCredits, int bet, RedBlack redBlackBet, byte generatedLotto,
                            long arsenalCredits, long lottoCredits, Condition condition) {

        // если лото позволяет дробление
        if (checkForWinningsLotto(lottoCredits)) {
            if (generatedLotto == Constants.LOTTO) return point(player, playerCredits, lottoCredits);
            if (generatedLotto == Constants.SUPER_LOTTO) return superLotto(player, playerCredits, lottoCredits);
        }

        Enums redBlackConvert = Converter.convert(generatedLotto);

        int resultCredits = bet * Constants.FOR_USER_CALCULATIONS;

        // если игрок выиграл
        if (redBlackConvert.equals(redBlackBet)) {
            condition.setBias(Constants.BIAS);
            player.setCredits(playerCredits + resultCredits);

            arsenalRepo.save(new Arsenal(arsenalCredits - resultCredits));
            conditionRepo.save(condition);
            playerRepo.save(player);

            return new Win(Prize.WIN, bet);
        }

        player.setCredits(playerCredits - resultCredits);

        lottoRepo.save(new Lotto(lottoCredits + resultCredits));
        playerRepo.save(player);

        return new Win(Prize.ZERO, 0);
    }



    private boolean checkForWinningsLotto(long lottoCredits) {
        return lottoCredits >= Constants.MINIMUM_LOTO_FOR_DRAWING_POSSIBILITIES;
    }
}
