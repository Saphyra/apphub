package com.github.saphyra.apphub.api.platform.monitoring.client;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.MonitoringEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "monitoring", url = "${serviceUrls.monitoring}")
public interface MonitoringClient {
    @PutMapping(MonitoringEndpoints.MONITORING_REPORT_METRICS)
    void reportMetrics(@PathVariable("service") String service, @RequestBody List<PutMetricsRequest> entries);
}
