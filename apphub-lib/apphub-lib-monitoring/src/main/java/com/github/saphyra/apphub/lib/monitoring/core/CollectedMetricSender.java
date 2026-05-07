package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.monitoring.core.agggregator.MetricPropertyAggregatorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CollectedMetricSender {
    private final CollectedMetricClient collectedMetricClient;
    private final MetricRegistry metricRegistry;
    private final Map<AggregationStrategy, MetricPropertyAggregatorStrategy> aggregators;

    CollectedMetricSender(CollectedMetricClient collectedMetricClient, MetricRegistry metricRegistry, List<MetricPropertyAggregatorStrategy> aggregators) {
        this.collectedMetricClient = collectedMetricClient;
        this.metricRegistry = metricRegistry;
        this.aggregators = aggregators.stream()
            .collect(Collectors.toMap(MetricPropertyAggregatorStrategy::getAggregationStrategy, aggregator -> aggregator));
    }

    public void sendCollectedMetrics() {
        List<PutMetricsRequest> metrics = metricRegistry.getMetricsToSend();

        List<PutMetricsRequest> toSend = aggregate(metrics);

        collectedMetricClient.send(toSend);
    }

    private List<PutMetricsRequest> aggregate(List<PutMetricsRequest> metrics) {
        return metrics.stream()
            .collect(Collectors.groupingBy(putMetricsRequest -> new AggregationKey(
                putMetricsRequest.getFeature(),
                putMetricsRequest.getFunctionality(),
                putMetricsRequest.getTimestamp()
            )))
            .entrySet()
            .stream()
            .map(entry -> aggregate(entry.getKey(), entry.getValue()))
            .toList();
    }

    private PutMetricsRequest aggregate(AggregationKey key, List<PutMetricsRequest> metrics) {
        Map<String, List<MetricPropertyModel>> propertyMap = metrics.stream()
            .flatMap(putMetricsRequest -> putMetricsRequest.getProperties().stream())
            .collect(Collectors.groupingBy(MetricPropertyModel::getKey));

        return PutMetricsRequest.builder()
            .feature(key.feature())
            .functionality(key.functionality())
            .timestamp(key.timestamp())
            .properties(aggregate(propertyMap))
            .build();
    }

    private List<MetricPropertyModel> aggregate(Map<String, List<MetricPropertyModel>> propertyMap) {
        return propertyMap.entrySet()
            .stream()
            .map(e -> aggregate(e.getKey(), e.getValue()))
            .toList();
    }

    private MetricPropertyModel aggregate(String key, List<MetricPropertyModel> properties) {
        AggregationStrategy aggregationStrategy = getAggregationStrategy(properties);

        List<Double> values = properties.stream()
            .map(MetricPropertyModel::getValue)
            .toList();

        Double aggregatedValue = aggregators.get(aggregationStrategy)
            .apply(values);
        return MetricPropertyModel.builder()
            .key(key)
            .value(aggregatedValue)
            .aggregationStrategy(aggregationStrategy)
            .build();
    }

    private AggregationStrategy getAggregationStrategy(List<MetricPropertyModel> properties) {
        List<AggregationStrategy> aggregationStrategies = properties.stream()
            .map(MetricPropertyModel::getAggregationStrategy)
            .distinct()
            .toList();

        if (aggregationStrategies.size() != 1) {
            throw new IllegalStateException("Properties have multiple aggregationStrategies: " + aggregationStrategies);
        }

        return aggregationStrategies.getFirst();
    }

    private record AggregationKey(Feature feature, String functionality, LocalDateTime timestamp) {
    }
}
