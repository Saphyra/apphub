package com.github.saphyra.apphub.service.feature.elite_base.processor.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@ConfigurationProperties("elite-base")
@Configuration
@Data
public class EliteBaseProcessorProperties {
    private Duration messageProcessorRetryDelay;
    private Integer messageProcessorMaxRetryCount;
    private Integer searchPageSize;
    private CacheProperties cache;
}
