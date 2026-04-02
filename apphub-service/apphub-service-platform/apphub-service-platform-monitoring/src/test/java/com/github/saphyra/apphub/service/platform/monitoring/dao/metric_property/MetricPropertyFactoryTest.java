package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MetricPropertyFactoryTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String KEY = "key";


    private final MetricPropertyFactory underTest = new MetricPropertyFactory();

    @Test
    void create() {
        assertThat(underTest.create(METRIC_ID, KEY, AggregationStrategy.SUM))
            .returns(METRIC_ID, MetricProperty::getMetricId)
            .returns(KEY, MetricProperty::getProperty)
            .returns(AggregationStrategy.SUM, MetricProperty::getAggregationStrategy);
    }
}