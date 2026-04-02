package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetricConverterTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String FUNCTIONALITY = "functionality";
    private static final String METRIC_ID_STRING = "metric-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MetricConverter underTest;

    @Test
    void convertDomain() {
        Metric domain = Metric.builder()
            .metricId(METRIC_ID)
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY)
            .build();

        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(METRIC_ID_STRING, MetricEntity::getMetricId)
            .returns(Feature.ELITE_BASE_MESSAGE_PROCESSING, MetricEntity::getFeature)
            .returns(FUNCTIONALITY, MetricEntity::getFunctionality);
    }

    @Test
    void convertEntity() {
        MetricEntity entity = MetricEntity.builder()
            .metricId(METRIC_ID_STRING)
            .feature(Feature.ELITE_BASE_MESSAGE_PROCESSING)
            .functionality(FUNCTIONALITY)
            .build();

        given(uuidConverter.convertEntity(METRIC_ID_STRING)).willReturn(METRIC_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(METRIC_ID, Metric::getMetricId)
            .returns(Feature.ELITE_BASE_MESSAGE_PROCESSING, Metric::getFeature)
            .returns(FUNCTIONALITY, Metric::getFunctionality);
    }
}