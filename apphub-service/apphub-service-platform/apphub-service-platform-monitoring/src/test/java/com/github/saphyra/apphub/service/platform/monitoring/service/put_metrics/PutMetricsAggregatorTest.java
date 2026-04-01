package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.agggregator.MetricPropertyAggregatorStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PutMetricsAggregatorTest {
    private static final String KEY = "key";
    private static final Double VALUE = 2d;
    private static final Double AGGREGATED_VALUE = 3d;

    @Mock
    private MetricPropertyAggregatorStrategy metricPropertyAggregatorStrategy;

    private PutMetricsAggregator underTest;

    @Mock
    private MetricPropertyModel propertyModel;

    @BeforeEach
    void setUp() {
        given(metricPropertyAggregatorStrategy.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        underTest = new PutMetricsAggregator(List.of(metricPropertyAggregatorStrategy));
    }

    @Test
    void aggregate() {
        given(propertyModel.getKey()).willReturn(KEY);
        given(propertyModel.getValue()).willReturn(VALUE);
        given(propertyModel.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);
        given(metricPropertyAggregatorStrategy.apply(List.of(VALUE, VALUE))).willReturn(AGGREGATED_VALUE);

        assertThat(underTest.aggregate(List.of(List.of(propertyModel), List.of(propertyModel))))
            .hasSize(1)
            .containsEntry(KEY, AGGREGATED_VALUE);
    }
}