package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MonitoringScheduler {
    private final CommonConfigProperties commonConfigProperties;
    private final EventGatewayApiClient eventGatewayApi;

    @Scheduled(cron = "${interval.monitoring.aggregateSecondMetrics}")
    void aggregateSecondMetrics() {
        String eventName = MonitoringEvent.AGGREGATE_SECOND_METRICS;
        sendEvent(eventName);
    }

    @Scheduled(cron = "${interval.monitoring.aggregateMinuteMetrics}")
    void aggregateMinuteMetrics() {
        String eventName = MonitoringEvent.AGGREGATE_MINUTE_METRICS;
        sendEvent(eventName);
    }

    @Scheduled(cron = "${interval.monitoring.deleteExpiredMetrics}")
    void deleteExpiredMetrics() {
        String eventName = MonitoringEvent.DELETE_EXPIRED_METRICS;
        sendEvent(eventName);
    }

    @Scheduled(initialDelayString = "${initialDelay}", fixedRateString = "${interval.monitoring.reportMemoryStatus}")
    void reportMemoryStatus() {
        String eventName = MonitoringEvent.REPORT_MEMORY_STATUS;
        sendEvent(eventName);
    }

    @Scheduled(initialDelayString = "${initialDelay}", fixedRateString = "${interval.monitoring.sendCollectedMetrics}")
    void sendCollectedMetrics() {
        String eventName = MonitoringEvent.SEND_COLLECTED_METRICS;
        sendEvent(eventName);
    }

    private void sendEvent(String eventName) {
        log.info("Sending event with name {}", eventName);

        eventGatewayApi.sendEvent(
            SendEventRequest.builder()
                .eventName(eventName)
                .build(),
            commonConfigProperties.getDefaultLocale()
        );
    }
}
