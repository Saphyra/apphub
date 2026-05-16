package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebSocketCleanupSchedulerTest {
    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @InjectMocks
    private WebSocketCleanupScheduler underTest;

    @Test
    public void webSocketCleanup() {

        underTest.webSocketCleanup();

        verify(eventGatewayApi).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.WEB_SOCKET_CONNECTION_CLEANUP_EVENT).build());
    }

    @Test
    public void webSocketPing() {

        underTest.webSocketPing();

        verify(eventGatewayApi).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.WEB_SOCKET_SEND_PING_EVENT).build());
    }
}