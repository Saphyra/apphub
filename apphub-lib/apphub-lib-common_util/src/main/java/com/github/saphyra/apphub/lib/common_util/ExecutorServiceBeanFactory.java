package com.github.saphyra.apphub.lib.common_util;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public class ExecutorServiceBeanFactory {
    private static final SleepService SLEEP_SERVICE = new SleepService();

    public static ExecutorServiceBean create(ExecutorService executorService) {
        return new ExecutorServiceBean(executorService, SLEEP_SERVICE);
    }
}
