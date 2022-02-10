package com.github.saphyra.apphub.service.platform.event_gateway.service.local_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.monitoring.MemoryMonitoringEventController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MemoryMonitoringLocalEventProcessor implements LocalEventProcessor {
    private final MemoryMonitoringEventController memoryMonitoringEventController;

    @Override
    public boolean shouldProcess(String eventName) {
        return EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE.equals(eventName);
    }

    @Override
    public void process(SendEventRequest<?> request) {
        memoryMonitoringEventController.sendMemoryStatus();
    }
}
