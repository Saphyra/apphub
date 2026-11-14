package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.GetMetricsResponse;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.Metric;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric.MetricDao;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricService;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service.MetricServiceDao;
import com.github.saphyra.apphub.service.platform.monitoring.service.MetricDataQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MonitoringControllerImplTest {
    private static final String SERVICE = "service";
    private static final String FUNCTIONALITY = "functionality";
    private static final UUID METRIC_ID = UUID.randomUUID();

    @Mock
    private MetricDao metricDao;

    @Mock
    private MetricServiceDao metricServiceDao;

    @Mock
    private MetricDataQueryService metricDataQueryService;

    @InjectMocks
    private MonitoringControllerImpl underTest;

    @Mock
    private PutMetricsRequest request;

    @Mock
    private AccessToken accessToken;

    @Mock
    private Metric metric;

    @Mock
    private MetricService metricService;

    @Mock
    private GetMetricsResponse getMetricsResponse;

    @Test
    void getFeatures() {
        given(metricDao.getFeatures()).willReturn(List.of(Feature.ELITE_BASE_MESSAGE_PROCESSING));

        assertThat(underTest.getFeatures(accessToken)).containsExactly(Feature.ELITE_BASE_MESSAGE_PROCESSING);
    }

    @Test
    void getFunctionalities() {
        given(metricDao.getFunctionalitiesOfFeature(Feature.ELITE_BASE_MESSAGE_PROCESSING)).willReturn(List.of(FUNCTIONALITY));

        assertThat(underTest.getFunctionalities(Feature.ELITE_BASE_MESSAGE_PROCESSING, accessToken)).containsExactly(FUNCTIONALITY);
    }

    @Test
    void getServices() {
        given(metricDao.getByFeatureAndOptionalFunctionality(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY)).willReturn(List.of(metric));
        given(metric.getMetricId()).willReturn(METRIC_ID);
        given(metricServiceDao.getByMetricIds(List.of(METRIC_ID))).willReturn(List.of(metricService, metricService));
        given(metricService.getService()).willReturn(SERVICE);

        assertThat(underTest.getServices(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, accessToken)).containsExactly(SERVICE);
    }

    @Test
    void getMetrics() {
        given(metricDataQueryService.getMetrics(MetricDataType.MINUTE, Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, SERVICE)).willReturn(List.of(getMetricsResponse));

        assertThat(underTest.getMetrics(MetricDataType.MINUTE, Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, SERVICE, accessToken)).containsExactly(getMetricsResponse);
    }
}