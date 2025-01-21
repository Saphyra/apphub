package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.AdminPanelEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class PerformanceReportingEventController {
    private final PerformanceReportingTopicStatusCache performanceReportingTopicStatusCache;

    @PostMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_EVENT_TOPIC_STATUS_MODIFIED)
    void topicStatusModifiedEvent(@RequestBody SendEventRequest<PerformanceReportingTopicStatus> event) {
        PerformanceReportingTopicStatus status = event.getPayload();
        log.info("Performance reporting status updated: {}", status);
        performanceReportingTopicStatusCache.update(status.getTopic(), status.getExpiration());
    }
}
