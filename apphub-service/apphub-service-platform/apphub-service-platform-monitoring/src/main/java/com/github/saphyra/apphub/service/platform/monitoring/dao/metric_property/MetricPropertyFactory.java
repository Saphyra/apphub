package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricPropertyFactory {
    public MetricProperty create(UUID metricId, String key, AggregationStrategy aggregationStrategy) {
        return MetricProperty.builder()
            .metricId(metricId)
            .property(key)
            .aggregationStrategy(aggregationStrategy)
            .build();
    }
}
