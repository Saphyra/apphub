package com.github.saphyra.apphub.service.admin_panel.performance_reporting.dao;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PerformanceReport {
    private final PerformanceReportingTopic topic;
    private final String key;
    private final Long value;
    private final LocalDateTime createdAt;
}
