package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricDataFactory;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PutMetricsServiceTest {
    private static final String SERVICE = "service";
    private static final String FUNCTIONALITY = "functionality";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String PROPERTY_KEY = "property-key";
    private static final Double PROPERTY_VALUE = 23.2;

    @Mock
    private MetricDao metricDao;

    @Mock
    private MetricServiceDao metricServiceDao;

    @Mock
    private MetricDataDao metricDataDao;

    @Mock
    private PutMetricsPropertyValidator putMetricsPropertyValidator;

    @Mock
    private MetricServiceFactory metricServiceFactory;

    @Mock
    private MetricDataFactory metricDataFactory;

    @InjectMocks
    private PutMetricsService underTest;

    @Mock
    private PutMetricsRequest request;

    @Mock
    private Metric metric;

    @Mock
    private MetricService metricService;

    @Mock
    private MetricPropertyModel propertyModel;

    @Mock
    private MetricData metricData;

    @Test
    void putMetrics() {
        given(request.getTimestamp()).willReturn(TIMESTAMP);
        given(request.getProperties()).willReturn(List.of(propertyModel));
        given(request.getFeature()).willReturn(Feature.ELITE_BASE_MESSAGE_PROCESSING);
        given(request.getFunctionality()).willReturn(FUNCTIONALITY);

        given(propertyModel.getKey()).willReturn(PROPERTY_KEY);
        given(propertyModel.getValue()).willReturn(PROPERTY_VALUE);

        given(metricDao.findOrCreate(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).willReturn(metric);
        given(metric.getMetricId()).willReturn(METRIC_ID);
        given(metricServiceFactory.create(METRIC_ID, SERVICE)).willReturn(metricService);

        Map<String, Double> properties = Map.of(PROPERTY_KEY, PROPERTY_VALUE);
        given(metricDataFactory.createSecond(METRIC_ID, SERVICE, TIMESTAMP.withNano(0), properties)).willReturn(metricData);


        underTest.putMetrics(SERVICE, request);

        then(metricServiceDao).should().save(metricService);
        then(putMetricsPropertyValidator).should().saveOrVerifyProperties(METRIC_ID, List.of(propertyModel));
        then(metricDataDao).should().save(metricData);
    }
}