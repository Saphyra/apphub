package com.github.saphyra.apphub.lib.common_util;

public class SleepService {
    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
