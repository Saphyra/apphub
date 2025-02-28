package com.github.saphyra.apphub.lib.common_util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApplicationContextProxy {
    private final ConfigurableApplicationContext applicationContext;

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public ConfigurableApplicationContext getContext() {
        return applicationContext;
    }
}
