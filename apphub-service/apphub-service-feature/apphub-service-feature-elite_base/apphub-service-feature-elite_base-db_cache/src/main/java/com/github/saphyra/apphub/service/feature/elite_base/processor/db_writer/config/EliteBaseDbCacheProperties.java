package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@ConfigurationProperties(prefix = "elite-base")
@Configuration
@Data
public class EliteBaseDbCacheProperties {
    private Duration processedMessageExpiration;
    private Duration messageExpiration;
    private Integer messageProcessorBatchSize;
}
