package com.github.saphyra.apphub.service.custom.elite_base.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.EliteBaseEndpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class EliteBaseEventProcessorRegistry implements EventProcessorRegistry {
    private final String host;

    public EliteBaseEventProcessorRegistry(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.ELITE_BASE_PROCESS_MESSAGES)
                .url(EliteBaseEndpoints.EVENT_PROCESS_MESSAGES)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.ELITE_BASE_RESET_UNHANDLED_MESSAGES)
                .url(EliteBaseEndpoints.EVENT_RESET_UNHANDLED_MESSAGES)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.ELITE_BASE_DELETE_EXPIRED_MESSAGES)
                .url(EliteBaseEndpoints.EVENT_DELETE_EXPIRED_MESSAGES)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.MIGRATION_ELITE_BASE_RESET_MESSAGE_STATUS_ERROR)
                .url(EliteBaseEndpoints.EVENT_MIGRATION_ELITE_BASE_RESET_MESSAGE_STATUS_ERROR)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.ELITE_BASE_ORPHANED_RECORD_CLEANUP)
                .url(EliteBaseEndpoints.EVENT_MIGRATION_ELITE_BASE_ORPHANED_RECORD_CLEANUP)
                .build()
        );
    }
}
