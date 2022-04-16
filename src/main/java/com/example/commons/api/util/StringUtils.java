package com.example.commons.api.util;

import java.text.MessageFormat;

public interface StringUtils {

    public static boolean isEmptyTrim(String value) {
        return (value == null || "".equals(value.trim()));
    }

    public static String format(String text, String... parameters) {
        return MessageFormat.format(text, (Object[]) parameters);
    }

}
