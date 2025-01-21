package com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PerformanceReportRequest {
    private PerformanceReportingTopic topic;
    private String key;
    private Long value;
}
