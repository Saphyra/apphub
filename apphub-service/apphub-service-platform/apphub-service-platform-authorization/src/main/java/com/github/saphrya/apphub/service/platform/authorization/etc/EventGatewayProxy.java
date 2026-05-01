package com.github.saphrya.apphub.service.platform.authorization.etc;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventGatewayProxy {
    private final EventGatewayApiClient eventGatewayApiClient;
    private final CommonConfigProperties commonConfigProperties;

    public void sendAccessTokenInvalidatedEvent(UUID accessTokenId) {
        SendEventRequest<List<UUID>> request = SendEventRequest.<List<UUID>>builder()
            .eventName(EmptyEvent.ACCESS_TOKENS_INVALIDATED)
            .payload(List.of(accessTokenId))
            .build()
            .blockingRequest(true);

        eventGatewayApiClient.sendEvent(request, commonConfigProperties.getDefaultLocale());
    }
}
