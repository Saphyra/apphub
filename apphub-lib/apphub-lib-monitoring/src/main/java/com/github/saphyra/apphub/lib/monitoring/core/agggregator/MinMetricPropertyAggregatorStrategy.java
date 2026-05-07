package com.github.saphyra.apphub.lib.monitoring.core.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class MinMetricPropertyAggregatorStrategy implements MetricPropertyAggregatorStrategy {
    @Override
    public AggregationStrategy getAggregationStrategy() {
        return AggregationStrategy.MIN;
    }

    @Override
    public Double apply(List<Double> doubles) {
        double min = Double.POSITIVE_INFINITY;

        for (Double d : doubles) {
            if (d < min) {
                min = d;
            }
        }

        return min;
    }
}
