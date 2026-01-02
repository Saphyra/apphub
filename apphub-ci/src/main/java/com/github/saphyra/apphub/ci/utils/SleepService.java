package com.github.saphyra.apphub.ci.utils;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class SleepService {
    @SneakyThrows
    public void sleep(int millis) {
        Thread.sleep(millis);
    }
}
