package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventGatewayProxy {
    private final EventGatewayApiClient eventGatewayClient;
    private final LocaleProvider localeProvider;

    public <T> void sendEvent(String eventName, T payload, boolean blockingRequest) {
        SendEventRequest<T> event = SendEventRequest.<T>builder()
            .eventName(eventName)
            .payload(payload)
            .build()
            .blockingRequest(blockingRequest);

        eventGatewayClient.sendEvent(event, localeProvider.getOrDefault());
    }
}
