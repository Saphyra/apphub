package com.github.saphyra.apphub.lib.common_util;

import java.util.Arrays;
import java.util.Objects;

public class CommonUtils {
    public static String withLeadingZeros(int in, int expectedLength) {
        return withLeadingZeros(String.valueOf(in), expectedLength);
    }

    public static String withLeadingZeros(String in, int expectedLength) {
        StringBuilder result = new StringBuilder(in);

        while (result.length() < expectedLength) {
            result.insert(0, "0");
        }

        return result.toString();
    }

    @SafeVarargs
    public static <T> T firstNotNull(T... objects) {
        return Arrays.stream(objects)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
