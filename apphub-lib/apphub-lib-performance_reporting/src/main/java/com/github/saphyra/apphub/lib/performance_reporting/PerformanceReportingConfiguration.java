package com.github.saphyra.apphub.lib.performance_reporting;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.AdminPanelEndpoints;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan
@EnableEventProcessor
public class PerformanceReportingConfiguration implements EventProcessorRegistry {
    private final String host;

    PerformanceReportingConfiguration(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(PerformanceReportingTopicStatus.EVENT_NAME)
                .url(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_EVENT_TOPIC_STATUS_MODIFIED)
                .build()
        );
    }
}
