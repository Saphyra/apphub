package com.github.saphyra.apphub.service.user.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
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
                .url(Endpoints.EVENT_DELETE_EXPIRED_ACCESS_TOKENS)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(RefreshAccessTokenExpirationEvent.EVENT_NAME)
                .url(Endpoints.EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(DeleteAccountEvent.EVENT_NAME)
                .url(Endpoints.EVENT_DELETE_ACCOUNT)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(EmptyEvent.TRIGGER_ACCOUNT_DELETION)
                .url(Endpoints.EVENT_TRIGGER_ACCOUNT_DELETION)
                .build(),
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(EmptyEvent.TRIGGER_REVOKE_EXPIRED_BANS)
                .url(Endpoints.EVENT_TRIGGER_REVOKE_EXPIRED_BANS)
                .build()
        );
    }
}
