package com.github.saphyra.apphub.service.user.settings;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

@ConfigurationProperties
@Configuration
@Data
@Slf4j
public class UserSettingProperties {
    private Map<String, Map<String, String>> settings;

    @PostConstruct
    public void postConstruct() {
        log.info("UserSettingProperties read: {}", this);
    }
}
