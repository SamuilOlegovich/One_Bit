package com.samuilolegovich.util;

import java.util.Locale;

public class LocaleHelper {
    public static final String RUSSIA_RU = "RU";
    public static final String RUSSIA = "ru";

    public static Locale getLocale(String language) {
        if (language.equalsIgnoreCase(RUSSIA) || language.equalsIgnoreCase(RUSSIA_RU))
            return new Locale("ru", "RU");
        return new Locale("en", "EN");
    }
}
