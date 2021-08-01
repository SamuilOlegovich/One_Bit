package com.samuilolegovich.domain.util;

import com.samuilolegovich.domain.Player;

public abstract class MessageHelper {
    public static String getPlayerUserName(Player player) {
        return player != null ? player.getUserName() : "<none>";
    }

}
