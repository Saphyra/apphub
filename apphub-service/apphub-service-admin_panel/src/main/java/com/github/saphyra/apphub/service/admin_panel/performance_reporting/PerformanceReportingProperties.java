package com.github.saphyra.apphub.service.admin_panel.performance_reporting;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
@Slf4j
public class PerformanceReportingProperties {
    @Value("${performanceReporting.cleanupRateSeconds}")
    private long cleanupRateSeconds;

    @Value("${performanceReporting.reportExpiration}")
    private Duration reportExpiration;

    @Value("${performanceReporting.topicExpiration}")
    private Duration topicExpiration;

    @PostConstruct
    void postConstruct() {
        log.info("{}", this);
    }
}
