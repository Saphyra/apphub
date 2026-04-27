package com.github.saphyra.apphub.lib.monitoring.core.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AverageMetricPropertyAggregatorStrategyTest {
    private final com.github.saphyra.apphub.lib.monitoring.core.agggregator.AverageMetricPropertyAggregatorStrategy underTest = new com.github.saphyra.apphub.lib.monitoring.core.agggregator.AverageMetricPropertyAggregatorStrategy(new com.github.saphyra.apphub.lib.monitoring.core.agggregator.SumMetricPropertyAggregatorStrategy());

    @Test
    void getAggregationStrategy() {
        assertThat(underTest.getAggregationStrategy()).isEqualTo(AggregationStrategy.AVERAGE);
    }

    @Test
    void apply() {
        assertThat(underTest.apply(List.of(2d, 4d))).isEqualTo(3d);
    }
}

