package com.github.saphyra.apphub.api.platform.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PutMetricsRequest {
    private String service;
    private Feature feature;
    private Functionality functionality;
    private LocalDateTime timestamp;
    private Map<String, MetricPropertyModel> properties = new HashMap<>();
}
