package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.admin_panel.client.MonitoringClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryStatusUpdateTriggerScheduler {
    private final MonitoringClient monitoringClient;
    private final CommonConfigProperties commonConfigProperties;

    @Scheduled(initialDelayString = "${initialDelay}", fixedRateString = "${interval.platform.memoryStatusUpdateTrigger}")
    public void triggerMemoryStatusUpdate() {
        log.debug("Triggering memory status update...");
        monitoringClient.triggerMemoryStatusUpdate(commonConfigProperties.getDefaultLocale());
    }
}
