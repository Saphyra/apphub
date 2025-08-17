package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class CalendarMigrationEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public CalendarMigrationEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(MigrationConstants.EVENT_NAME)
                .url(MigrationConstants.URL)
                .build()
        );
    }
}
