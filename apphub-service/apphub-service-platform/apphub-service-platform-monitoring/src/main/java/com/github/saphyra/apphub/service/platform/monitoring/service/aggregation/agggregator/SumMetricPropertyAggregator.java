package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.agggregator;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SumMetricPropertyAggregator implements MetricPropertyAggregator {
    @Override
    public AggregationStrategy getAggregationStrategy() {
        return AggregationStrategy.SUM;
    }

    @Override
    public Double apply(List<Double> doubles) {
        return doubles.stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
}
