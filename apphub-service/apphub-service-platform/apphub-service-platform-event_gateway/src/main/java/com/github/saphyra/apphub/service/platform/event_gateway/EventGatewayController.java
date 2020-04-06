package com.github.saphyra.apphub.service.platform.event_gateway;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.server.EventGatewayApiServer;
import com.github.saphyra.apphub.service.platform.event_gateway.service.heartbeat.HeartbeatService;
import com.github.saphyra.apphub.service.platform.event_gateway.service.register.RegisterProcessorService;
import com.github.saphyra.apphub.service.platform.event_gateway.service.send_event.EventSendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO int test
public class EventGatewayController implements EventGatewayApiServer {
    private final EventSendingService eventSendingService;
    private final HeartbeatService heartbeatService;
    private final RegisterProcessorService registerProcessorService;

    @Override
    public void registerProcessor(RegisterProcessorRequest registerProcessorRequest) {
        log.info("Registering eventProcessor: {}", registerProcessorRequest);
        registerProcessorService.registerProcessor(registerProcessorRequest);
    }

    @Override
    public void heartbeat(String serviceName) {
        log.info("Heartbeat arrived from {}", serviceName);
        heartbeatService.heartbeat(serviceName);
    }

    @Override
    public void sendEvent(SendEventRequest<?> sendEventRequest) {
        log.info("SendEventRequest arrived with eventName {}", sendEventRequest.getEventName());
        log.debug("SendEventRequest: {}", sendEventRequest);
        eventSendingService.sendEvent(sendEventRequest);
    }
}
