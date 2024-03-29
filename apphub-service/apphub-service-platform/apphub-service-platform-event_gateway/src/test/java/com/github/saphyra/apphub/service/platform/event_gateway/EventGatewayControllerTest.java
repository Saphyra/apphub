package com.github.saphyra.apphub.service.platform.event_gateway;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.service.platform.event_gateway.service.heartbeat.ProcessHeartbeatService;
import com.github.saphyra.apphub.service.platform.event_gateway.service.register.RegisterProcessorService;
import com.github.saphyra.apphub.service.platform.event_gateway.service.send_event.EventSendingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventGatewayControllerTest {
    private static final String SERVICE_NAME = "service-name";

    @Mock
    private EventSendingService eventSendingService;

    @Mock
    private ProcessHeartbeatService processHeartbeatService;

    @Mock
    private RegisterProcessorService registerProcessorService;

    @InjectMocks
    private EventGatewayController underTest;

    @Mock
    private RegisterProcessorRequest registerProcessorRequest;

    @Mock
    private SendEventRequest<?> sendEventRequest;

    @Test
    public void registerProcessor() {
        underTest.registerProcessor(registerProcessorRequest);

        verify(registerProcessorService).registerProcessor(registerProcessorRequest);
    }

    @Test
    public void heartbeat() {
        underTest.heartbeat(SERVICE_NAME);

        verify(processHeartbeatService).heartbeat(SERVICE_NAME);
    }

    @Test
    public void sendEvent() {
        underTest.sendEvent(sendEventRequest);

        verify(eventSendingService).sendEvent(sendEventRequest);
    }
}