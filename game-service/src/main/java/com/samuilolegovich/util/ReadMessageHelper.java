package com.samuilolegovich.util;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReadMessageHelper {
    private static final String BASE_NAME = "messages.message";

    public static String getMessageForPlayer(@NotNull String key, @NotNull Locale locale) {
        return new String(ResourceBundle.getBundle(BASE_NAME, locale)
                .getString(key)
                .getBytes(StandardCharsets.ISO_8859_1)
                ,StandardCharsets.UTF_8
        );
    }
}
