package com.github.saphyra.apphub.lib.common_util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class ExecutorServiceBeanFactory {
    private final SleepService sleepService;

    public ExecutorServiceBean create(ExecutorService executorService) {
        return new ExecutorServiceBean(executorService, sleepService);
    }
}
