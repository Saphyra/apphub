package com.github.saphyra.apphub.api.platform.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PutMetricsRequest {
    private Feature feature;
    private String functionality;
    private LocalDateTime timestamp;
    private List<MetricPropertyModel> properties = new ArrayList<>();
}
