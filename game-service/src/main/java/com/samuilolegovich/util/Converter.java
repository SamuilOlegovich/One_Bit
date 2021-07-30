package com.samuilolegovich.util;

import com.samuilolegovich.enums.Enums;
import com.samuilolegovich.enums.Prize;
import com.samuilolegovich.enums.RedBlack;
import com.samuilolegovich.util.Constants;

public class Converter {
    // конвертирует число в енум
    public static Enums convert(byte b) {
        if (b == Constants.LOTTO) return Prize.LOTTO;
        if (b == Constants.SUPER_LOTTO) return Prize.SUPER_LOTTO;
        if (b > Constants.START && b < Constants.MIDDLE) return RedBlack.RED;
        if (b > Constants.MIDDLE && b < Constants.STOP) return RedBlack.BLACK;
        return Prize.ZERO;
    }

    public static long convertForUserCalculation(long creditsPlayer) {
        return (long) (creditsPlayer / Constants.FOR_USER_CALCULATIONS);
    }
}
