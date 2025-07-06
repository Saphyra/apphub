package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;

import java.util.List;

public interface PerformanceReportingProxy {
    List<PerformanceReportingTopicStatus> getTopicStatus();

    void report(PerformanceReportingTopic topic, String key, long value);
}
