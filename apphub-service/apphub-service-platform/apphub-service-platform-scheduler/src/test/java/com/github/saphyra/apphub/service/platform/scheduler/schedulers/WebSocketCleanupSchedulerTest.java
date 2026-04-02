package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebSocketCleanupSchedulerTest {
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @InjectMocks
    private WebSocketCleanupScheduler underTest;

    @Test
    public void webSocketCleanup() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.webSocketCleanup();

        verify(eventGatewayApi).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.WEB_SOCKET_CONNECTION_CLEANUP_EVENT).build(), LOCALE);
    }

    @Test
    public void webSocketPing() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.webSocketPing();

        verify(eventGatewayApi).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.WEB_SOCKET_SEND_PING_EVENT).build(), LOCALE);
    }
}