package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.client.PerformanceReportingClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
class DefaultPerformanceReportingProxy implements PerformanceReportingProxy {
    private final CommonConfigProperties commonConfigProperties;
    private final PerformanceReportingClient performanceReportingClient;
    private final SleepService sleepService;

    @Override
    public List<PerformanceReportingTopicStatus> getTopicStatus() {
        for (int i = 0; i < 5; i++) {
            try {
                return performanceReportingClient.getTopicStatus(commonConfigProperties.getDefaultLocale());
            } catch (Exception e) {
                log.warn("Failed to get performance reporting topic status. Retrying...");
                sleepService.sleep((i + 1) * 1000);
            }
        }

        log.error("Failed retrieving performance reporting topic status after multiple attempts.");

        return List.of();
    }

    @Override
    public void report(PerformanceReportingTopic topic, String key, long value) {
        PerformanceReportRequest request = PerformanceReportRequest.builder()
            .topic(topic)
            .key(key)
            .value(value)
            .build();
        performanceReportingClient.report(request, commonConfigProperties.getDefaultLocale());
    }
}
