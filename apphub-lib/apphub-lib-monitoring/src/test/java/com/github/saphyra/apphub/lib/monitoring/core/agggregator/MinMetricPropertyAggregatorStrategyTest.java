package com.github.saphyra.apphub.lib.monitoring.core.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.lib.monitoring.core.agggregator.MinMetricPropertyAggregatorStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MinMetricPropertyAggregatorStrategyTest {
    private final MinMetricPropertyAggregatorStrategy underTest = new MinMetricPropertyAggregatorStrategy();

    @Test
    void getAggregationStrategy() {
        assertThat(underTest.getAggregationStrategy()).isEqualTo(AggregationStrategy.MIN);
    }

    @Test
    void apply() {
        assertThat(underTest.apply(List.of(2d, 3.1))).isEqualTo(2d);
    }
}

