package com.github.saphyra.apphub.service.platform.monitoring.config;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
@ConfigurationProperties(value = "monitoring")
@Data
public class MonitoringProperties {
    private Duration metricExpirationDuration;
    private Map<MetricDataType, Migration> migration;

    @Data
    public static class Migration{
        private Duration expirationDuration;
        private Duration stepDuration;
    }
}
