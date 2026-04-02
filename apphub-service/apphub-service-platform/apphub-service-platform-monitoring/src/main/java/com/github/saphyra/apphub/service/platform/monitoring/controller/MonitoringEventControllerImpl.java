package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.server.MonitoringEventController;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.ExpiredMetricCleanupService;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.MetricAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class MonitoringEventControllerImpl implements MonitoringEventController {
    private final MetricAggregationService metricAggregationService;
    private final ExpiredMetricCleanupService expiredMetricCleanupService;

    @Override
    public void aggregateSecondMetrics() {
        log.info("{} metrics aggregation started", MetricDataType.SECOND);

        metricAggregationService.aggregate(MetricDataType.SECOND);
    }

    @Override
    public void aggregateMinuteMetrics() {
        log.info("{} metrics aggregation started", MetricDataType.MINUTE);

        metricAggregationService.aggregate(MetricDataType.MINUTE);
    }

    @Override
    public void deleteExpiredMetrics() {
        log.info("Cleaning up expired metrics");

        expiredMetricCleanupService.cleanup();
    }
}
