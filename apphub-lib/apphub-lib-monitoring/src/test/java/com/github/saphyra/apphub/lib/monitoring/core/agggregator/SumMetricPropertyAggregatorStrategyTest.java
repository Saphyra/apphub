package com.github.saphyra.apphub.lib.monitoring.core.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SumMetricPropertyAggregatorStrategyTest {
    private final SumMetricPropertyAggregatorStrategy underTest = new SumMetricPropertyAggregatorStrategy();

    @Test
    void getAggregationStrategy() {
        assertThat(underTest.getAggregationStrategy()).isEqualTo(AggregationStrategy.SUM);
    }

    @Test
    void apply() {
        assertThat(underTest.apply(List.of(2d, 3.1))).isEqualTo(5.1);
    }
}