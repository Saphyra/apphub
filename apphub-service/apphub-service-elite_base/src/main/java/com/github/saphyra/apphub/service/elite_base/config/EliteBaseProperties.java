package com.github.saphyra.apphub.service.elite_base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@ConfigurationProperties("elite-base")
@Configuration
@Data
public class EliteBaseProperties {
    private Duration messageExpiration;
    private Integer messageProcessorBatchSize;
    private Integer messageProcessorSublistSize;
}
