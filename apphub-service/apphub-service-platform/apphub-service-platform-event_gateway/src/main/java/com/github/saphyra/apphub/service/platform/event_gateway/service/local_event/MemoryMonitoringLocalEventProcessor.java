package com.github.saphyra.apphub.service.platform.event_gateway.service.local_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.MonitoringEvent;
import com.github.saphyra.apphub.lib.monitoring.memory.MemoryStatusReporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryMonitoringLocalEventProcessor implements LocalEventProcessor {
    private final MemoryStatusReporter memoryStatusReporter;

    @Override
    public boolean shouldProcess(String eventName) {
        return MonitoringEvent.REPORT_MEMORY_STATUS.equals(eventName);
    }

    @Override
    public void process(SendEventRequest<?> request) {
        memoryStatusReporter.reportMemoryStatus();
    }
}
