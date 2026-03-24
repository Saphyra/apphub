package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.api.platform.monitoring.server.MonitoringController;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics.PutMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MonitoringControllerImpl implements MonitoringController {
    private final PutMetricsService putMetricsService;
    private final ErrorReporterService errorReporterService;

    @Override
    public void reportMetrics(List<PutMetricsRequest> entries) {
        entries.forEach(putMetricsRequest -> {
            try {
                log.info("Arrived: {}", putMetricsRequest);
                putMetricsService.putMetrics(putMetricsRequest);
            } catch (Exception e) {
                errorReporterService.report("Failed to put metrics" + putMetricsRequest, e);
            }
        });
    }
}
