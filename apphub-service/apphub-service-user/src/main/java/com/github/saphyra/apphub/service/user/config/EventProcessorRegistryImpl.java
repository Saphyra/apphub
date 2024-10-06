package com.github.saphyra.apphub.service.user.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
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
                .eventName(EmptyEvent.DELETE_EXPIRED_ACCESS_TOKENS_EVENT_NAME)
                .url(UserEndpoints.EVENT_DELETE_EXPIRED_ACCESS_TOKENS)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(RefreshAccessTokenExpirationEvent.EVENT_NAME)
                .url(UserEndpoints.EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(DeleteAccountEvent.EVENT_NAME)
                .url(GenericEndpoints.EVENT_DELETE_ACCOUNT)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.TRIGGER_ACCOUNT_DELETION)
                .url(UserEndpoints.EVENT_TRIGGER_ACCOUNT_DELETION)
                .build(),
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EmptyEvent.TRIGGER_REVOKE_EXPIRED_BANS)
                .url(UserEndpoints.EVENT_TRIGGER_REVOKE_EXPIRED_BANS)
                .build()
        );
    }
}
