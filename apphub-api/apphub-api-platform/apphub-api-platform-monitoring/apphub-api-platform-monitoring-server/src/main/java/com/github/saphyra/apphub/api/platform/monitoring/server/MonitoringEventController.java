package com.github.saphyra.apphub.api.platform.monitoring.server;

import com.github.saphyra.apphub.api.platform.monitoring.model.MonitoringEndpoints;
import org.springframework.web.bind.annotation.PostMapping;

public interface MonitoringEventController {
    @PostMapping(MonitoringEndpoints.MONITORING_EVENT_AGGREGATE_SECOND_METRICS)
    void aggregateSecondMetrics();

    @PostMapping(MonitoringEndpoints.MONITORING_EVENT_AGGREGATE_MINUTE_METRICS)
    void aggregateMinuteMetrics();

    @PostMapping(MonitoringEndpoints.MONITORING_EVENT_DELETE_EXPIRED_METRICS)
    void deleteExpiredMetrics();
}
