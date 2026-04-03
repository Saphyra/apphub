package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

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
class MetricServiceConverterTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String SERVICE = "service";
    private static final String METRIC_ID_STRING = "metric-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MetricServiceConverter underTest;

    @Test
    void convertDomain() {
        MetricService domain = MetricService.builder()
            .metricId(METRIC_ID)
            .service(SERVICE)
            .build();

        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(METRIC_ID_STRING, MetricServiceEntity::getMetricId)
            .returns(SERVICE, MetricServiceEntity::getService);
    }

    @Test
    void convertEntity() {
        MetricServiceEntity entity = MetricServiceEntity.builder()
            .metricId(METRIC_ID_STRING)
            .service(SERVICE)
            .build();

        given(uuidConverter.convertEntity(METRIC_ID_STRING)).willReturn(METRIC_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(METRIC_ID, MetricService::getMetricId)
            .returns(SERVICE, MetricService::getService);
    }
}