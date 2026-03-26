package com.github.saphyra.apphub.service.platform.monitoring.service.migration.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MaxMetricPropertyAggregator implements MetricPropertyAggregator {
    @Override
    public AggregationStrategy getAggregationStrategy() {
        return AggregationStrategy.MAX;
    }

    @Override
    public Double apply(List<Double> doubles) {
        double max = Double.NEGATIVE_INFINITY;

        for (Double d : doubles) {
            if (d > max) {
                max = d;
            }
        }

        return max;
    }
}
