package com.github.saphyra.apphub.integration.common.framework;

public class SleepUtil {
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
