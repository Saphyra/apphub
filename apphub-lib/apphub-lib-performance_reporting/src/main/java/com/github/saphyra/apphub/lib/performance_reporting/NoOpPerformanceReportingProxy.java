package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@Profile("test")
class NoOpPerformanceReportingProxy implements PerformanceReportingProxy {
    @Override
    public List<PerformanceReportingTopicStatus> getTopicStatus() {
        return Collections.emptyList();
    }

    @Override
    public void report(PerformanceReportingTopic topic, String key, long value) {
        log.debug("Not reporting topic {} with key {} and value {}", topic, key, value);
    }
}
