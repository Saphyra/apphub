package com.github.saphyra.apphub.ci.utils;

public class Utils {
    public static String withLeading(String value, int length, String filler) {
        if (filler.length() == 0) {
            throw new IllegalArgumentException("filler must be at least one character long.");
        }

        StringBuilder result = new StringBuilder(value);
        while (result.length() < length) {
            result.insert(0, filler);
        }
        return result.toString();
    }
}
