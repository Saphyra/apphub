package com.github.saphyra.apphub.service.etc.admin_panel.proxy;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventGatewayProxyTest {
    @Mock
    private EventGatewayApiClient eventGatewayApiClient;

    @InjectMocks
    private EventGatewayProxy underTest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Test
    void sendEvent() {
        underTest.sendEvent(sendEventRequest);

        then(eventGatewayApiClient).should().sendEvent(sendEventRequest);
    }
}