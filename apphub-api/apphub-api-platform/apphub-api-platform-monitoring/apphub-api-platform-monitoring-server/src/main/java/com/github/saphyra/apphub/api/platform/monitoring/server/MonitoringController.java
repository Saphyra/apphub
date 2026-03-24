package com.github.saphyra.apphub.api.platform.monitoring.server;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.MonitoringEndpoints;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface MonitoringController {
    @PutMapping(MonitoringEndpoints.MONITORING_REPORT_METRICS)
    void reportMetrics(@RequestBody List<PutMetricsRequest> entries);
}
