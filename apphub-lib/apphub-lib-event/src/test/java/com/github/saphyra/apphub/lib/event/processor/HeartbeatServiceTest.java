package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HeartbeatServiceTest {
    private static final String SERVICE_NAME = "service-name";

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    private HeartbeatService underTest;

    @Before
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