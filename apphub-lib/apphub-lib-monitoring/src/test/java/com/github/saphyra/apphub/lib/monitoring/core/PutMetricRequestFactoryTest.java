package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PutMetricRequestFactoryTest {
    private static final String FUNCTIONALITY = "functionality";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

    private final PutMetricRequestFactory underTest = new PutMetricRequestFactory();

    @Mock
    private MetricPropertyModel propertyModel;

    @Test
    void create() {
        assertThat(underTest.create(Feature.ELITE_BASE_MESSAGE_PROCESSING, FUNCTIONALITY, TIMESTAMP, List.of(propertyModel)))
            .returns(Feature.ELITE_BASE_MESSAGE_PROCESSING, PutMetricsRequest::getFeature)
            .returns(FUNCTIONALITY, PutMetricsRequest::getFunctionality)
            .returns(TIMESTAMP, PutMetricsRequest::getTimestamp)
            .returns(List.of(propertyModel), PutMetricsRequest::getProperties);
    }
}