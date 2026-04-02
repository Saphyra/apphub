package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
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
class MetricPropertyConverterTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String PROPERTY = "property";
    private static final String METRIC_ID_STRING = "metric-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MetricPropertyConverter underTest;

    @Test
    void convertDomain() {
        MetricProperty domain = MetricProperty.builder()
            .metricId(METRIC_ID)
            .property(PROPERTY)
            .aggregationStrategy(AggregationStrategy.SUM)
            .build();

        given(uuidConverter.convertDomain(METRIC_ID)).willReturn(METRIC_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(AggregationStrategy.SUM, MetricPropertyEntity::getAggregationStrategy)
            .extracting(MetricPropertyEntity::getId)
            .returns(METRIC_ID_STRING, MetricPropertyId::getMetricId)
            .returns(PROPERTY, MetricPropertyId::getProperty);
    }

    @Test
    void convertEntity() {
        MetricPropertyEntity entity = MetricPropertyEntity.builder()
            .id(MetricPropertyId.builder()
                .metricId(METRIC_ID_STRING)
                .property(PROPERTY)
                .build())
            .aggregationStrategy(AggregationStrategy.SUM)
            .build();

        given(uuidConverter.convertEntity(METRIC_ID_STRING)).willReturn(METRIC_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(METRIC_ID, MetricProperty::getMetricId)
            .returns(PROPERTY, MetricProperty::getProperty)
            .returns(AggregationStrategy.SUM, MetricProperty::getAggregationStrategy);
    }
}