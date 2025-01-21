package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.client.PerformanceReportingClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class PerformanceReportingProxy {
    private final CommonConfigProperties commonConfigProperties;
    private final PerformanceReportingClient performanceReportingClient;

    public List<PerformanceReportingTopicStatus> getTopicStatus() {
        return performanceReportingClient.getTopicStatus(commonConfigProperties.getDefaultLocale());
    }

    public void report(PerformanceReportingTopic topic, String key, long value) {
        PerformanceReportRequest request = PerformanceReportRequest.builder()
            .topic(topic)
            .key(key)
            .value(value)
            .build();
        performanceReportingClient.report(request, commonConfigProperties.getDefaultLocale());
    }
}
