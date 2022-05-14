package com.samuilolegovich.domain.util;

import com.samuilolegovich.domain.User;

public abstract class MessageHelper {
    public static String getPlayerUserName(User user) {
        return user != null ? user.getUserName() : "<none>";
    }

}
