package com.blueharbor.hotel.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-()\\s]{7,}$");

    private ValidationUtil() {
    }

    public static boolean isEmail(String value) {
        return value != null && EMAIL.matcher(value).matches();
    }

    public static boolean isPhone(String value) {
        return value != null && PHONE.matcher(value).matches();
    }
}
