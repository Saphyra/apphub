package com.github.saphyra.apphub.service.platform.event_gateway;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.service.platform.event_gateway.service.heartbeat.HeartbeatService;
import com.github.saphyra.apphub.service.platform.event_gateway.service.register.RegisterProcessorService;
import com.github.saphyra.apphub.service.platform.event_gateway.service.send_event.EventSendingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventGatewayControllerTest {
    private static final String SERVICE_NAME = "service-name";

    @Mock
    private EventSendingService eventSendingService;

    @Mock
    private HeartbeatService heartbeatService;

    @Mock
    private RegisterProcessorService registerProcessorService;

    @InjectMocks
    private EventGatewayController underTest;

    @Mock
    private RegisterProcessorRequest registerProcessorRequest;

    @Mock
    private SendEventRequest sendEventRequest;

    @Test
    public void registerProcessor() {
        underTest.registerProcessor(registerProcessorRequest);

        verify(registerProcessorService).registerProcessor(registerProcessorRequest);
    }

    @Test
    public void heartbeat() {
        underTest.heartbeat(SERVICE_NAME);

        verify(heartbeatService).heartbeat(SERVICE_NAME);
    }

    @Test
    public void sendEvent() {
        underTest.sendEvent(sendEventRequest);

        verify(eventSendingService).sendEvent(sendEventRequest);
    }
}