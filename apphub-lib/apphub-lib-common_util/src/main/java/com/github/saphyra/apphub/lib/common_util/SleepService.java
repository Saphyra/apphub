package com.github.saphyra.apphub.lib.common_util;

public class SleepService {
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
