package com.samuilolegovich.model;

import com.samuilolegovich.dto.PlayerInfoFoBetDto;
import com.samuilolegovich.dto.WonOrNotWon;
import com.samuilolegovich.enums.RedBlack;

public interface Bets {
    public WonOrNotWon calculateTheWin(PlayerInfoFoBetDto playerInfoFoBetDto, int bet, RedBlack redBlack);
}
