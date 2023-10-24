package com.github.saphyra.apphub.service.notebook.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NotebookEventProcessorRegistryImpl implements EventProcessorRegistry {
    private final String host;

    public NotebookEventProcessorRegistryImpl(
        @Value("${event.serviceHost}") String host
    ) {
        this.host = host;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return Arrays.asList(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(DeleteAccountEvent.EVENT_NAME)
                .url(Endpoints.EVENT_DELETE_ACCOUNT)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.NOTEBOOK_MIGRATE_CHECKLISTS)
                .url(Endpoints.NOTEBOOK_EVENT_MIGRATION_CHECKLIST)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.NOTEBOOK_MIGRATE_TABLES)
                .url(Endpoints.NOTEBOOK_EVENT_MIGRATION_TABLE)
                .build()
        );
    }
}
