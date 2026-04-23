package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.MonitoringEndpoints;
import com.github.saphyra.apphub.lib.monitoring.memory.MemoryStatusReporter;
import com.github.saphyra.apphub.lib.monitoring.util.InMemoryDaoMonitor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Builder
public class MonitoringEventController {
    private final MemoryStatusReporter memoryStatusReporter;
    private final CollectedMetricSender collectedMetricSender;
    private final InMemoryDaoMonitor inMemoryDaoMonitor;

    @PostMapping(MonitoringEndpoints.EVENT_REPORT_MEMORY_STATUS)
    void reportMemoryStatus() {
        log.debug("Reporting memory status");
        memoryStatusReporter.reportMemoryStatus();
    }

    @PostMapping(MonitoringEndpoints.EVENT_REPORT_IN_MEMORY_DAO_STATUS)
    void reportInMemoryDaoStatus() {
        log.debug("Reporting InMemoryDao status");
        inMemoryDaoMonitor.report();
    }

    @PostMapping(MonitoringEndpoints.EVENT_SEND_COLLECTED_METRICS)
    void sendCollectedMetrics() {
        log.debug("Sending collected metrics");
        collectedMetricSender.sendCollectedMetrics();
    }
}
