package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class MetricProperty {
    private final UUID metricId;
    private final String property;
    private final AggregationStrategy aggregationStrategy;
}
