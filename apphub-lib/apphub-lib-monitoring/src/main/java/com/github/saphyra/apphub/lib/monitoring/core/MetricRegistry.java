package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.FUNCTIONALITY_METRIC_COUNT;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_AVERAGE_SIZE;
import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.KEY_MAX_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricRegistry {
    @Getter
    private final List<PutMetricsRequest> registry = new Vector<>();

    private final DateTimeUtil dateTimeUtil;
    private final PutMetricRequestFactory putMetricRequestFactory;

    public void reportMetric(Feature feature, String functionality, List<MetricPropertyModel> properties) {
        LocalDateTime timestamp = dateTimeUtil.getCurrentDateTime()
            .withNano(0);

        PutMetricsRequest request = putMetricRequestFactory.create(feature, functionality, timestamp, properties);

        registry.add(request);
    }

    public List<PutMetricsRequest> getMetricsToSend() {
        LocalDateTime timestamp = dateTimeUtil.getCurrentDateTime()
            .withNano(0);

        Map<LocalDateTime, List<PutMetricsRequest>> result = registry.stream()
            .filter(metric -> metric.getTimestamp().isBefore(timestamp)) //Send metrics created before the current second
            .collect(Collectors.groupingBy(PutMetricsRequest::getTimestamp));

        log.debug("MetricRegistry size before cleanup: {}", registry.size());
        //Remove
        result.values().forEach(registry::removeAll);
        log.debug("MetricRegistry size after cleanup: {}", registry.size());

        return result.entrySet()
            .stream()
            .flatMap(entry -> compute(entry.getKey(), entry.getValue()))
            .toList();
    }

    //Adds an extra metric that keeps the number of metrics returned
    private Stream<PutMetricsRequest> compute(LocalDateTime timestamp, List<PutMetricsRequest> metrics) {
        double count = metrics.size();

        MetricPropertyModel maxCount = MetricPropertyModel.builder()
            .key(KEY_MAX_SIZE)
            .value(count)
            .aggregationStrategy(AggregationStrategy.MAX)
            .build();

        MetricPropertyModel averageCount = MetricPropertyModel.builder()
            .key(KEY_AVERAGE_SIZE)
            .value(count)
            .aggregationStrategy(AggregationStrategy.AVERAGE)
            .build();

        PutMetricsRequest putMetricsRequest = putMetricRequestFactory.create(
            Feature.MONITORING_METRICS,
            FUNCTIONALITY_METRIC_COUNT,
            timestamp,
            List.of(maxCount, averageCount)
        );

        return Stream.concat(metrics.stream(), Stream.of(putMetricsRequest));
    }
}
