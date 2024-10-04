package com.github.saphyra.apphub.service.calendar.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalendarEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public CalendarEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(DeleteAccountEvent.EVENT_NAME)
                .url(GenericEndpoints.EVENT_DELETE_ACCOUNT)
                .build()
        );
    }
}
