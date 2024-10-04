package com.github.saphyra.apphub.service.skyxplore.game.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SkyXploreGameEventProcessorRegistryImpl implements EventProcessorRegistry {
    private final String host;

    public SkyXploreGameEventProcessorRegistryImpl(
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
                .url(GenericEndpoints.EVENT_DELETE_ACCOUNT)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.SKYXPLORE_GAME_CLEANUP_EVENT_NAME)
                .url(GenericSkyXploreEndpoints.EVENT_SKYXPLORE_GAME_CLEANUP)
                .build()
        );
    }
}
