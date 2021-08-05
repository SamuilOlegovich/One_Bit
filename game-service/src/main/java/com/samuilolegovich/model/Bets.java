package com.samuilolegovich.model;

import com.samuilolegovich.dto.WonOrNotWon;
import com.samuilolegovich.enums.RedBlack;

public interface Bets {
    public WonOrNotWon calculateTheWin(Player player, int bet, RedBlack redBlack);
}
