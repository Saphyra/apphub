package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PerformanceReportSender {
    private final PerformanceReportingTopicStatusCache performanceReportingTopicStatusCache;
    private final DateTimeUtil dateTimeUtil;
    private final PerformanceReportingProxy performanceReportingProxy;
    private final ExecutorServiceBean executorServiceBean;

    void sendReport(PerformanceReportingTopic topic, String key, long value) {
        try {
            executorServiceBean.execute(() -> {
                performanceReportingTopicStatusCache.get(topic)
                    .filter(expiration -> expiration.isAfter(dateTimeUtil.getCurrentDateTime()))
                    .ifPresent(localDateTime -> {
                        performanceReportingProxy.report(topic, key, value);
                    });
            });
        } catch (Exception e) {
            log.error("Failed sending performanceReport", e);
        }
    }
}
