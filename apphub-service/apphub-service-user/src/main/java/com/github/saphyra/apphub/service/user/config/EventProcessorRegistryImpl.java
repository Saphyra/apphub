package com.github.saphyra.apphub.service.user.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EventProcessorRegistryImpl implements EventProcessorRegistry {
    private final String serviceName;

    public EventProcessorRegistryImpl(
        @Value("${spring.application.name}") String serviceName
    ) {
        this.serviceName = serviceName;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return Arrays.asList(
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(EmptyEvent.DELETE_EXPIRED_ACCESS_TOKENS_EVENT_NAME)
                .url(Endpoints.DELETE_EXPIRED_ACCESS_TOKENS_EVENT)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(RefreshAccessTokenExpirationEvent.EVENT_NAME)
                .url(Endpoints.REFRESH_ACCESS_TOKEN_EXPIRATION_EVENT)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(DeleteAccountEvent.EVENT_NAME)
                .url(Endpoints.DELETE_ACCOUNT_EVENT)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(PageVisitedEvent.EVENT_NAME)
                .url(Endpoints.PAGE_VISITED_EVENT)
                .build()
        );
    }
}
