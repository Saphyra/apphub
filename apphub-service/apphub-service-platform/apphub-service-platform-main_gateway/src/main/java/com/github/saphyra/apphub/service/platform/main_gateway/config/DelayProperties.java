package com.github.saphyra.apphub.service.platform.main_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "delay.filter")
@Data
public class DelayProperties {
    private List<DelayedPath> paths;
}
