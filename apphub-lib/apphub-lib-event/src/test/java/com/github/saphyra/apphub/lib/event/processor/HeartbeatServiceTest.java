package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HeartbeatServiceTest {
    private static final String SERVICE_NAME = "service-name";

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    private HeartbeatService underTest;

    @BeforeEach
    public void setUp() {
        underTest = HeartbeatService.builder()
            .eventGatewayApi(eventGatewayApi)
            .serviceName(SERVICE_NAME)
            .build();
    }

    @Test
    public void sendHeartbeat() {
        underTest.sendHeartbeat();

        verify(eventGatewayApi).heartbeat(SERVICE_NAME);
    }
}