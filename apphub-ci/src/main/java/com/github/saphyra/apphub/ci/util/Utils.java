package com.github.saphyra.apphub.ci.util;

public class Utils {
    public static String withLeading(Object value, int length, String filler) {
        if (filler.isEmpty()) {
            throw new IllegalArgumentException("filler must be at least one character long.");
        }

        StringBuilder result = new StringBuilder(value.toString());
        while (result.length() < length) {
            result.insert(0, filler);
        }
        return result.toString();
    }

    public static String withLeadingZeros(int in, int expectedLength) {
        return withLeadingZeros(String.valueOf(in), expectedLength);
    }

    public static String withLeadingZeros(String in, int expectedLength) {
        return withLeading(in, expectedLength, "0");
    }
}
