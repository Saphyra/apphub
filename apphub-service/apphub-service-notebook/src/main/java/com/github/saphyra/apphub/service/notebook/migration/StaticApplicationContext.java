package com.github.saphyra.apphub.service.notebook.migration;

import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaticApplicationContext {
    private static ApplicationContextProxy APPLICATION_CONTEXT_PROXY;

    private final ApplicationContextProxy applicationContextProxy;

    public static ApplicationContextProxy getApplicationContextProxy() {
        return APPLICATION_CONTEXT_PROXY;
    }

    @PostConstruct
    void setStaticProxy() {
        APPLICATION_CONTEXT_PROXY = applicationContextProxy;
    }
}
