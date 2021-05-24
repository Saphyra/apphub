package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
class HeartbeatService {
    private final String serviceName;
    private final EventGatewayApiClient eventGatewayApi;

    @Builder
    HeartbeatService(
        @Value("${spring.application.name}") String serviceName,
        EventGatewayApiClient eventGatewayApi
    ) {
        this.serviceName = serviceName;
        this.eventGatewayApi = eventGatewayApi;
    }

    @Scheduled(fixedRateString = "${event.processor.heartbeat.interval:60000}")
    void sendHeartbeat() {
        log.debug("Sending heartbeat...");
        eventGatewayApi.heartbeat(serviceName);
        log.debug("Heartbeat sent.");
    }
}
