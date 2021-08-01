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
public class AnswerToBetDto {
    private Enums informationForBet;
    private Enums winningCombination;
    private Enums claimedCombination;
    private Long totalPlayerCredits;
    private Long win;
    private String comment;

}
