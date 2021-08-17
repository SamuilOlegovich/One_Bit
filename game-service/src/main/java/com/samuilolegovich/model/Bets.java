package com.samuilolegovich.model;

import com.samuilolegovich.domain.User;
import com.samuilolegovich.dto.WonOrNotWon;
import com.samuilolegovich.enums.RedBlack;

public interface Bets {
    WonOrNotWon calculateTheWin(User user, int bet, RedBlack redBlack);
}
