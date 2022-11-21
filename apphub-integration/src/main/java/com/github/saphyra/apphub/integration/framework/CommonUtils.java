package com.github.saphyra.apphub.integration.framework;

public class CommonUtils {
    public static String withLeadingZeros(int number, int length) {
        String num = String.valueOf(number);

        while (num.length() < length) {
            num = "0" + num;
        }

        return num;
    }
}
