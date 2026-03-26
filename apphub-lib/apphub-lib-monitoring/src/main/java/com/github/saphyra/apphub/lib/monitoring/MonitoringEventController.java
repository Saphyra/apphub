package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.lib.config.common.endpoints.MonitoringEndpoints;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Builder
//TODO unit test
public class MonitoringEventController {
    private final MemoryStatusReporter memoryStatusReporter;
    private final CollectedMetricSender collectedMetricSender;

    @PostMapping(MonitoringEndpoints.EVENT_REPORT_MEMORY_STATUS)
    void reportMemoryStatus() {
        log.debug("Reporting memory status");
        memoryStatusReporter.reportMemoryStatus();
    }

    @PostMapping(MonitoringEndpoints.EVENT_SEND_COLLECTED_METRICS)
    void sendCollectedMetrics() {
        log.debug("Sending collected metrics");
        collectedMetricSender.sendCollectedMetrics();
    }
}
