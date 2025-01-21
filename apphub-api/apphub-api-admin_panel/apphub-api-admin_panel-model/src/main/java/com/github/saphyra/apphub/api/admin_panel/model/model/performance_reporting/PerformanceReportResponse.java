package com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PerformanceReportResponse {
    private String key;
    private Long min;
    private Long max;
    private Double average;
    private Integer count;
}
