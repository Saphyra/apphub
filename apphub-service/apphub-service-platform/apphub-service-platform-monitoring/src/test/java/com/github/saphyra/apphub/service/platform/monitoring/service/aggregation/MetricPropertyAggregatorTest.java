package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.lib.monitoring.core.agggregator.MetricPropertyAggregatorStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetricPropertyAggregatorTest {
    private static final UUID METRIC_ID = UUID.randomUUID();
    private static final String KEY = "key";
    private static final Double AGGREGATED_VALUE = 42.0;
    private static final Double VALUE = 52.2;

    @Mock
    private MetricPropertyDao metricPropertyDao;

    @Mock
    private MetricPropertyAggregatorStrategy aggregatorStrategy;

    private MetricPropertyAggregator underTest;

    @Mock
    private MetricProperty metricProperty;

    @Mock
    private MetricData metricData;

    @BeforeEach
    void setUp() {
        given(aggregatorStrategy.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);

        underTest = new MetricPropertyAggregator(metricPropertyDao, List.of(aggregatorStrategy));
    }

    @Test
    void aggregateProperties() {
        given(metricPropertyDao.getByMetricId(METRIC_ID)).willReturn(List.of(metricProperty));
        given(metricProperty.getProperty()).willReturn(KEY);
        given(metricProperty.getAggregationStrategy()).willReturn(AggregationStrategy.SUM);
        given(metricData.getProperties()).willReturn(Map.of(KEY, VALUE));
        given(aggregatorStrategy.apply(List.of(VALUE))).willReturn(AGGREGATED_VALUE);

        assertThat(underTest.aggregateProperties(METRIC_ID, List.of(metricData)))
            .hasSize(1)
            .containsEntry(KEY, AGGREGATED_VALUE);
    }
}