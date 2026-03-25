package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class MetricData {
    private final UUID metricDataId;
    private final UUID metricId;
    private final String service;
    private final MetricDataType type;
    private final LocalDateTime timestamp;
    private final Map<String, Double> properties;
}
