package com.github.saphyra.apphub.ci.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApplicationContextProxy {
    private final ApplicationContext applicationContext;

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
