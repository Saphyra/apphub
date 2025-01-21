package com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PerformanceReportingTopicStatus {
    public static final String EVENT_NAME = "performance-reporting-topic-modified";

    private PerformanceReportingTopic topic;
    private LocalDateTime expiration;
    private Boolean enabled;
}
