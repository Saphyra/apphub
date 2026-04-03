package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.ExpiredMetricCleanupService;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.MetricAggregationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MonitoringEventControllerImplTest {
    @Mock
    private MetricAggregationService metricAggregationService;

    @Mock
    private ExpiredMetricCleanupService expiredMetricCleanupService;

    @InjectMocks
    private MonitoringEventControllerImpl underTest;

    @Test
    void aggregateSecondMetrics() {
        underTest.aggregateSecondMetrics();

        then(metricAggregationService).should().aggregate(MetricDataType.SECOND);
    }

    @Test
    void aggregateMinuteMetrics() {
        underTest.aggregateMinuteMetrics();

        then(metricAggregationService).should().aggregate(MetricDataType.MINUTE);
    }

    @Test
    void deleteExpiredMetrics() {
        underTest.deleteExpiredMetrics();

        then(expiredMetricCleanupService).should().cleanup();
    }
}