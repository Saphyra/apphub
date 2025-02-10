package com.github.saphyra.apphub.service.custom.elite_base.util;

public class Utils {
    public static Double nullIfZero(double aDouble) {
        if (aDouble == 0) {
            return null;
        }

        return aDouble;
    }
}
