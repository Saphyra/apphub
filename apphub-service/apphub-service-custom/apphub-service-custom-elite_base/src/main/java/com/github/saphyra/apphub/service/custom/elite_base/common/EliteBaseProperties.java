package com.github.saphyra.apphub.service.custom.elite_base.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@ConfigurationProperties("elite-base")
@Configuration
@Data
public class EliteBaseProperties {
    private Duration messageExpiration;
    private Duration processedMessageExpiration;
    private Integer messageProcessorBatchSize;
    private Integer messageProcessorSublistSize;
    private Duration retryDelay;
    private Integer maxRetryCount;
}
