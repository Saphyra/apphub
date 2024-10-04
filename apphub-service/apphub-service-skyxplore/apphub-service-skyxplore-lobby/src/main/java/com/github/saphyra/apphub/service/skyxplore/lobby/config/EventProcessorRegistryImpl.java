package com.github.saphyra.apphub.service.skyxplore.lobby.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
        return Arrays.asList(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.SKYXPLORE_LOBBY_CLEANUP_EVENT_NAME)
                .url(GenericSkyXploreEndpoints.EVENT_SKYXPLORE_LOBBY_CLEANUP)
                .build()
        );
    }
}
