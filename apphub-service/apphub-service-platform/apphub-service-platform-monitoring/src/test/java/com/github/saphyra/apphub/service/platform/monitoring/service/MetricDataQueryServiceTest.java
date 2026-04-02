package com.github.saphyra.apphub.service.platform.monitoring.service;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.GetMetricsResponse;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.monitoring.config.MonitoringProperties;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetricDataQueryServiceTest {
    private static final String FUNCTIONALITY = "functionality";
    private static final String SERVICE = "service";
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final Duration EXPIRATION_DURATION = Duration.ofSeconds(23);
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final UUID METRIC_DATA_ID = UUID.randomUUID();
    private static final Long TIMESTAMP = 233L;

    @Mock
    private MetricDao metricDao;

    @Mock
    private MetricDataDao metricDataDao;

    @Mock
    private MonitoringProperties monitoringProperties;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private MetricDataQueryService underTest;

    @Mock
    private Metric metric;

    @Mock
    private MonitoringProperties.Aggregation aggregationProperties;

    @Test
    void metricNotFound() {
        given(metricDao.getByFeatureAndOptionalFunctionality(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).willReturn(List.of());

        ExceptionValidator.validateNotLoggedException(
            catchThrowable(() -> underTest.getMetrics(MetricDataType.SECOND, Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, SERVICE)),
            HttpStatus.BAD_REQUEST,
            ErrorCode.INVALID_PARAM,
            new BiWrapper<>(Feature.ELITE_BASE_MESSAGE_PROCESSING.name(), "invalid_combination"),
            new BiWrapper<>(FUNCTIONALITY, "invalid_combination")
        );
    }

    @Test
    void getMetrics() {
        given(metricDao.getByFeatureAndOptionalFunctionality(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).willReturn(List.of(metric));
        given(metric.getMetricId()).willReturn(METRIC_ID);
        given(monitoringProperties.getAggregation()).willReturn(Map.of(MetricDataType.SECOND, aggregationProperties));
        given(aggregationProperties.getExpirationDuration()).willReturn(EXPIRATION_DURATION);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        Map<String, Double> properties = Map.of("asd", 3d);
        MetricData metricData = MetricData.builder()
            .metricDataId(METRIC_DATA_ID)
            .metricId(METRIC_ID)
            .service(SERVICE)
            .timestamp(CURRENT_TIME)
            .properties(properties)
            .build();
        given(metricDataDao.getByTypeAndMetricIdInAndServiceAfter(MetricDataType.SECOND, Set.of(METRIC_ID), SERVICE, CURRENT_TIME.minus(EXPIRATION_DURATION))).willReturn(List.of(metricData));
        given(dateTimeUtil.toEpochSecond(CURRENT_TIME)).willReturn(TIMESTAMP);
        given(metric.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(metric.getFunctionality()).willReturn(FUNCTIONALITY);

        CustomAssertions.singleListAssertThat(underTest.getMetrics(MetricDataType.SECOND, Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, SERVICE))
            .returns(METRIC_DATA_ID, GetMetricsResponse::getMetricDataId)
            .returns(Feature.ELITE_BASE_MESSAGE_PROCESSING, GetMetricsResponse::getFeature)
            .returns(FUNCTIONALITY, GetMetricsResponse::getFunctionality)
            .returns(SERVICE, GetMetricsResponse::getService)
            .returns(TIMESTAMP, GetMetricsResponse::getTimestamp)
            .returns(properties, GetMetricsResponse::getProperties);
    }
}