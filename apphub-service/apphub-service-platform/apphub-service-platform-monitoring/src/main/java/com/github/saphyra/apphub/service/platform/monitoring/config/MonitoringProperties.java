package com.github.saphyra.apphub.service.platform.monitoring.config;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
@ConfigurationProperties(value = "monitoring")
@Data
@Slf4j
public class MonitoringProperties {
    private Map<MetricDataType, Aggregation> aggregation;

    @PostConstruct
    void logProperties() {
        log.info("{}", this);
    }

    @Data
    public static class Aggregation {
        private Duration expirationDuration;
        private Duration stepDuration;
    }
}
