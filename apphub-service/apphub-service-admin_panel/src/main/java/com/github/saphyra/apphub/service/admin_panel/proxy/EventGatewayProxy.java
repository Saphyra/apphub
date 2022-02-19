package com.github.saphyra.apphub.service.admin_panel.proxy;

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
    private final EventGatewayApiClient eventGatewayApiClient;
    private final LocaleProvider localeProvider;

    public void sendEvent(SendEventRequest<?> sendEventRequest) {
        eventGatewayApiClient.sendEvent(sendEventRequest, localeProvider.getOrDefault());
    }
}
