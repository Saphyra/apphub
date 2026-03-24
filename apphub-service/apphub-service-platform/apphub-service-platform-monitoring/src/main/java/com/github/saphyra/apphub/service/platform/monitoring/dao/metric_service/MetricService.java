package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class MetricService {
    private final UUID metricId;
    private final String service;
}
