package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MessageSenderPingRequestScheduler {
    private final CommonConfigProperties commonConfigProperties;
    private final EventGatewayApiClient eventGatewayApi;

    @Scheduled(fixedRateString = "${interval.platform.messageSender.pingRequest}")
    void pingRequest() {
        String eventName = EmptyEvent.MESSAGE_SENDER_PING_REQUEST_EVENT_NAME;
        log.info("Sending event with name {}", eventName);
        eventGatewayApi.sendEvent(
            SendEventRequest.builder()
                .eventName(eventName)
                .build(),
            commonConfigProperties.getDefaultLocale()
        );
    }
}