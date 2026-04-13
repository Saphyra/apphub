package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.lib.monitoring.MonitoringProperties.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricRegistry {
    private final Map<LocalDateTime, List<PutMetricsRequest>> registry = new ConcurrentHashMap<>();

    private final DateTimeUtil dateTimeUtil;
    private final PutMetricRequestFactory putMetricRequestFactory;

    public void reportMetric(Feature feature, String functionality, List<MetricPropertyModel> properties) {
        LocalDateTime timestamp = dateTimeUtil.getCurrentDateTime()
            .withNano(0);

        PutMetricsRequest request = putMetricRequestFactory.create(feature, functionality, timestamp, properties);

        List<PutMetricsRequest> bucket = registry.computeIfAbsent(timestamp, _ -> new Vector<>());
        bucket.add(request);
    }

    public List<List<PutMetricsRequest>> getMetricsToSend() {
        LocalDateTime timestamp = dateTimeUtil.getCurrentDateTime()
            .withNano(0);

        Map<LocalDateTime, List<PutMetricsRequest>> result = registry.entrySet()
            .stream()
            .filter(entry -> entry.getKey().isBefore(timestamp)) //Send metrics created before the current second
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> compute(entry.getKey(), entry.getValue())));

        result.forEach((t, _) -> registry.remove(t));

        return result.values()
            .stream()
            .toList();
    }

    //Adds an extra metric that keeps the number of metrics returned
    private List<PutMetricsRequest> compute(LocalDateTime timestamp, List<PutMetricsRequest> metrics) {
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

        return Stream.concat(metrics.stream(), Stream.of(putMetricsRequest))
            .toList();
    }
}
