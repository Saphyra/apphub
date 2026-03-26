package com.github.saphyra.apphub.service.platform.event_gateway.service.local_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import com.github.saphyra.apphub.lib.monitoring.CollectedMetricSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SendCollectedMetricsLocalEventProcessor implements LocalEventProcessor {
    private final CollectedMetricSender collectedMetricSender;

    @Override
    public boolean shouldProcess(String eventName) {
        return MonitoringEvent.SEND_COLLECTED_METRICS.equals(eventName);
    }

    @Override
    public void process(SendEventRequest<?> request) {
        log.debug("Processing local event {}", MonitoringEvent.SEND_COLLECTED_METRICS);
        collectedMetricSender.sendCollectedMetrics();
    }
}
