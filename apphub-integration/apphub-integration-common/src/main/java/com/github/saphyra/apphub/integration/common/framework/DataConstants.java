package com.github.saphyra.apphub.integration.common.framework;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataConstants {
    public static final String VALID_PASSWORD = "valid-password";
    public static final String VALID_PASSWORD2 = "valid-password2";
    public static final String INCORRECT_PASSWORD = "incorrect-password";
    public static final String TOO_SHORT_USERNAME = "to";
    public static final String TOO_LONG_USERNAME = Stream.generate(() -> "a")
        .limit(31)
        .collect(Collectors.joining());
    public static final String TOO_SHORT_PASSWORD = "passw";
    public static final String TOO_LONG_PASSWORD = Stream.generate(() -> "a")
        .limit(31)
        .collect(Collectors.joining());
    public static final String INVALID_CONFIRM_PASSWORD = "invalid-confirm-password";
    public static final String INVALID_EMAIL = "a@a.a";
}
