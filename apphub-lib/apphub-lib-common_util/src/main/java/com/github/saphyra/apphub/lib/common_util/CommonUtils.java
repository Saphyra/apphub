package com.github.saphyra.apphub.lib.common_util;

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
}
