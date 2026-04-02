package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MetricServiceFactoryTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String SERVICE = "service";


    private final MetricServiceFactory underTest = new MetricServiceFactory();

    @Test
    void create() {
        assertThat(underTest.create(METRIC_ID, SERVICE))
            .returns(METRIC_ID, MetricService::getMetricId)
            .returns(SERVICE, MetricService::getService);
    }
}