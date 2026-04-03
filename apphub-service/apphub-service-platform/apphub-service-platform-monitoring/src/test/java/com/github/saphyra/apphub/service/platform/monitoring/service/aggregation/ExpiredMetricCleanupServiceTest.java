package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ExpiredMetricCleanupServiceTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration METRIC_EXPIRATION_DURATION = Duration.ofDays(2);
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String SERVICE = "service";

    @Mock
    private MetricDataDao metricDataDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private MonitoringProperties monitoringProperties;

    @Mock
    private MetricDao metricDao;

    @Mock
    private MetricServiceDao metricServiceDao;

    @Mock
    private MetricPropertyDao metricPropertyDao;

    @InjectMocks
    private ExpiredMetricCleanupService underTest;

    @Test
    void cleanup() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(monitoringProperties.getMetricExpirationDuration()).willReturn(METRIC_EXPIRATION_DURATION);
        given(metricDataDao.getMetricIds()).willReturn(List.of(METRIC_ID));
        given(metricDataDao.getServices()).willReturn(List.of(SERVICE));

        underTest.cleanup();

        then(metricDataDao).should().deleteByTimestampBefore(CURRENT_TIME.minus(METRIC_EXPIRATION_DURATION));
        then(metricDao).should().deleteByMetricIdNotIn(List.of(METRIC_ID));
        then(metricServiceDao).should().deleteByMetricIdsNotIn(List.of(METRIC_ID));
        then(metricPropertyDao).should().deleteByMetricIdsNotIn(List.of(METRIC_ID));
        then(metricServiceDao).should().deleteByServiceNotIn(List.of(SERVICE));
    }
}