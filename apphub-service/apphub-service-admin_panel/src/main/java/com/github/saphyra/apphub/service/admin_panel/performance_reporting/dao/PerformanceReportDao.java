package com.github.saphyra.apphub.service.admin_panel.performance_reporting.dao;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.service.admin_panel.performance_reporting.PerformanceReportingProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceReportDao {
    private final Collection<PerformanceReport> DAO = new ConcurrentLinkedQueue<>();

    private final PerformanceReportingProperties properties;
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;
    private final DateTimeUtil dateTimeUtil;

    public List<PerformanceReport> getByTopic(PerformanceReportingTopic topic) {
        LocalDateTime expiration = getExpiration();

        return DAO.stream()
            .filter(performanceReport -> performanceReport.getTopic() == topic)
            .filter(performanceReport -> performanceReport.getCreatedAt().isAfter(expiration))
            .toList();
    }

    public void add(PerformanceReportingTopic topic, String key, Long value) {
        PerformanceReport report = PerformanceReport.builder()
            .topic(topic)
            .key(key)
            .value(value)
            .createdAt(dateTimeUtil.getCurrentDateTime())
            .build();

        DAO.add(report);
    }

    public void delete(PerformanceReportingTopic topic) {
        DAO.removeIf(performanceReport -> performanceReport.getTopic() == topic);
    }

    private void cleanup() {
        LocalDateTime expiration = getExpiration();

        DAO.removeIf(performanceReport -> performanceReport.getCreatedAt().isBefore(expiration));
    }

    private LocalDateTime getExpiration() {
        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
        return currentTime.minus(properties.getReportExpiration());
    }

    @PostConstruct
    void initiateCleanup() {
        scheduledExecutorServiceBean.scheduleFixedRate(this::cleanup, properties.getCleanupRateSeconds(), TimeUnit.SECONDS);
    }
}
