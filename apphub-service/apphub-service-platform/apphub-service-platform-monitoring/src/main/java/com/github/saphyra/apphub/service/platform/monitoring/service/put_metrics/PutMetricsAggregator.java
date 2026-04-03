package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.service.platform.monitoring.service.aggregation.agggregator.MetricPropertyAggregatorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
class PutMetricsAggregator {
    private final Map<AggregationStrategy, MetricPropertyAggregatorStrategy> aggregators;

    PutMetricsAggregator(List<MetricPropertyAggregatorStrategy> aggregators) {
        this.aggregators = aggregators.stream()
            .collect(Collectors.toMap(MetricPropertyAggregatorStrategy::getAggregationStrategy, aggregator -> aggregator));
    }

    Map<String, Double> aggregate(List<List<MetricPropertyModel>> properties) {
        return properties.stream()
            .flatMap(Collection::stream)
            //Group properties by key
            .collect(Collectors.groupingBy(MetricPropertyModel::getKey))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> aggregateProperties(e.getValue())));
    }

    private Double aggregateProperties(List<MetricPropertyModel> properties) {
        AggregationStrategy aggregationStrategy = properties.getFirst()
            .getAggregationStrategy();

        MetricPropertyAggregatorStrategy aggregator = aggregators.get(aggregationStrategy);

        List<Double> values = properties.stream()
            .map(MetricPropertyModel::getValue)
            .toList();

        return aggregator.apply(values);
    }
}
