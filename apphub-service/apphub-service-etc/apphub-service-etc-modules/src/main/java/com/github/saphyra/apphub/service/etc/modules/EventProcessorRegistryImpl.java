package com.github.saphyra.apphub.service.etc.modules;

import com.github.saphyra.apphub.api.etc.modules.model.response.ModulesEndpoints;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventProcessorRegistryImpl implements EventProcessorRegistry {
    private final String host;

    public EventProcessorRegistryImpl(
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
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.PLATFORM_DISABLED_ROLES_CHANGED)
                .url(ModulesEndpoints.EVENT_DISABLED_ROLES_CHANGED)
                .build()
        );
    }
}
