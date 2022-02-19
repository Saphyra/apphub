package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
class EventProcessorRegisterService {
    private final EventProcessorProperties eventProcessorProperties;
    private final List<EventProcessorRegistry> registries;
    private final EventGatewayApiClient eventGatewayApi;
    private final SleepService sleepService;

    @EventListener(ApplicationReadyEvent.class)
    void registerProcessors() {
        log.info("Registering eventProcessors...");
        registries.stream()
            .flatMap(eventProcessorRegistry -> eventProcessorRegistry.getRequests().stream())
            .forEach(this::register);
        log.info("EventProcessors registered.");
    }

    private void register(RegisterProcessorRequest registerProcessorRequest) {
        int tryCount = 0;
        log.info("Registering eventProcessor {}", registerProcessorRequest);
        RuntimeException ex = null;
        for (int i = 1; i <= 3; i++) {
            try {
                eventGatewayApi.registerProcessor(registerProcessorRequest);
                return;
            } catch (RuntimeException e) {
                log.debug("Registering eventProcessor {} failed for tryCount {}.", registerProcessorRequest, tryCount, e);
                log.warn("Registering eventProcessor {} failed for tryCount {}.", registerProcessorRequest, tryCount);
                ex = e;
                sleepService.sleep(eventProcessorProperties.getRegistrationFailureRetryDelay());
            }
        }
        throw ex;
    }
}
