package com.github.saphyra.apphub.proxy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@ConfigurationProperties
@Component
@Slf4j
@Data
public class ApplicationProperties {
    private String forwardedPort;

    @PostConstruct
    public void logConfig() {
        log.info("AppConfig: {}", this);
    }
}
