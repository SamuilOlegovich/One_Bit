package com.samuilolegovich.dto;

import com.samuilolegovich.enums.Enums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WonOrNotWon {
    private Enums declaredRate;
    private Enums replyToBet;
    private long userCredits;
    private long bet;
    private long win;
    private long totalLoansNow;
    private long totalLottoNow;
}
