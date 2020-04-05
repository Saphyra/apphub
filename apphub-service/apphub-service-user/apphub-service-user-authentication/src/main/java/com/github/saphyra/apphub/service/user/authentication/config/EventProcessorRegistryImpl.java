package com.github.saphyra.apphub.service.user.authentication.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import com.github.saphyra.apphub.lib.event.DeleteExpiredAccessTokensEvent;
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
                .eventName(DeleteExpiredAccessTokensEvent.EVENT_NAME)
                .url(Endpoint.DELETE_EXPIRED_ACCESS_TOKENS_EVENT)
                .build()
        );
    }
}
