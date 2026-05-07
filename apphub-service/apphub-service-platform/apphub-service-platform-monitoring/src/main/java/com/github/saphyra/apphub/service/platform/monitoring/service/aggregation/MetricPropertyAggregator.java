package com.github.saphyra.apphub.service.platform.monitoring.service.aggregation;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data.MetricData;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricProperty;
import com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property.MetricPropertyDao;
import com.github.saphyra.apphub.lib.monitoring.core.agggregator.MetricPropertyAggregatorStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
class MetricPropertyAggregator {
    private final MetricPropertyDao metricPropertyDao;
    private final Map<AggregationStrategy, MetricPropertyAggregatorStrategy> aggregators;

    MetricPropertyAggregator(MetricPropertyDao metricPropertyDao, List<MetricPropertyAggregatorStrategy> aggregators) {
        this.metricPropertyDao = metricPropertyDao;
        this.aggregators = aggregators.stream()
            .collect(Collectors.toMap(MetricPropertyAggregatorStrategy::getAggregationStrategy, data -> data));
    }

    Map<String, Double> aggregateProperties(UUID metricId, List<MetricData> metrics) {
        Map<String, AggregationStrategy> aggregationStrategies = metricPropertyDao.getByMetricId(metricId)
            .stream()
            .collect(Collectors.toMap(MetricProperty::getProperty, MetricProperty::getAggregationStrategy));

        return aggregationStrategies.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> aggregateProperty(entry.getValue(), entry.getKey(), metrics)));
    }

    private Double aggregateProperty(AggregationStrategy aggregationStrategy, String property, List<MetricData> metrics) {
        List<Double> values = metrics.stream()
            .filter(metricData -> metricData.getProperties().containsKey(property))
            .map(metricData -> metricData.getProperties().get(property))
            .toList();
        MetricPropertyAggregatorStrategy aggregator = aggregators.get(aggregationStrategy);

        return aggregator.apply(values);
    }

    @PostConstruct
    void verifyAggregators() {
        List<AggregationStrategy> missingAggregators = Arrays.stream(AggregationStrategy.values())
            .filter(aggregationStrategy -> !this.aggregators.containsKey(aggregationStrategy))
            .toList();

        if (!missingAggregators.isEmpty()) {
            throw new IllegalStateException("Missing aggregators for strategies: " + missingAggregators);
        }
    }
}
