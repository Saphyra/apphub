package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EliteBaseScheduler {
    private final CommonConfigProperties commonConfigProperties;
    private final EventGatewayApiClient eventGatewayApi;

    @Scheduled(initialDelayString = "${initialDelay}", fixedRateString = "${interval.eliteBase.processMessages}")
    void processMessages() {
        String eventName = EmptyEvent.ELITE_BASE_PROCESS_MESSAGES;
        log.info("Sending event with name {}", eventName);
        eventGatewayApi.sendEvent(
            SendEventRequest.builder()
                .eventName(eventName)
                .build(),
            commonConfigProperties.getDefaultLocale()
        );
    }

    @Scheduled(initialDelayString = "${initialDelay}", fixedRateString = "${interval.eliteBase.resetUnhandledMessages}")
    void resetUnhandledMessages() {
        String eventName = EmptyEvent.ELITE_BASE_RESET_UNHANDLED_MESSAGES;
        log.info("Sending event with name {}", eventName);
        eventGatewayApi.sendEvent(
            SendEventRequest.builder()
                .eventName(eventName)
                .build(),
            commonConfigProperties.getDefaultLocale()
        );
    }

    @Scheduled(initialDelayString = "${initialDelay}", fixedRateString = "${interval.eliteBase.deleteExpiredMessages}")
    void deleteExpiredMessages() {
        String eventName = EmptyEvent.ELITE_BASE_DELETE_EXPIRED_MESSAGES;
        log.info("Sending event with name {}", eventName);
        eventGatewayApi.sendEvent(
            SendEventRequest.builder()
                .eventName(eventName)
                .build(),
            commonConfigProperties.getDefaultLocale()
        );
    }
}
