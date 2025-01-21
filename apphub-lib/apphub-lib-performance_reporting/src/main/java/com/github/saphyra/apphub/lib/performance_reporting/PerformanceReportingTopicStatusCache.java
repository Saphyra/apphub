package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.lib.common_util.cache.AbstractCache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
class PerformanceReportingTopicStatusCache extends AbstractCache<PerformanceReportingTopic, LocalDateTime> {
    private final PerformanceReportingProxy performanceReportingProxy;

    PerformanceReportingTopicStatusCache(PerformanceReportingProxy performanceReportingProxy) {
        super(CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build());
        this.performanceReportingProxy = performanceReportingProxy;
    }

    @Override
    protected Optional<LocalDateTime> load(PerformanceReportingTopic topic) {
        return performanceReportingProxy.getTopicStatus()
            .stream()
            .filter(performanceReportingTopicStatus -> performanceReportingTopicStatus.getTopic() == topic)
            .map(PerformanceReportingTopicStatus::getExpiration)
            .findAny();
    }

    public void update(PerformanceReportingTopic topic, LocalDateTime expiration) {
        put(topic, expiration);
    }
}
