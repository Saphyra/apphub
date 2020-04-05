package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class EventProcessorRegisterService {
    private final EventProcessorRegistry registry;
    private final EventGatewayApiClient eventGatewayApi;

    @EventListener(ApplicationReadyEvent.class)
    void registerProcessors() {
        log.info("Registering eventProcessors...");
        registry.getRequests()
            .stream()
            .parallel()
            .forEach(eventGatewayApi::registerProcessor);
        log.info("EventProcessors registered.");
    }
}
