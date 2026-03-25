package com.github.saphyra.apphub.api.platform.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetMetricsResponse {
    private UUID metricDataId;
    private Feature feature;
    private String functionality;
    private String service;
    private LocalDateTime timestamp;
    private Map<String, Double> properties;
}
