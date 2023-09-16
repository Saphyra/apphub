package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.admin_panel.client.MonitoringClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Builder
public class MemoryMonitoringEventController {
    private final CommonConfigProperties commonConfigProperties;
    private final MonitoringClient monitoringClient;
    private final MemoryStatusModelFactory memoryStatusModelFactory;

    private final String serviceName;

    public MemoryMonitoringEventController(
        CommonConfigProperties commonConfigProperties,
        MonitoringClient monitoringClient,
        MemoryStatusModelFactory memoryStatusModelFactory,
        @Value("${spring.application.name}") String serviceName
    ) {
        this.commonConfigProperties = commonConfigProperties;
        this.monitoringClient = monitoringClient;
        this.memoryStatusModelFactory = memoryStatusModelFactory;
        this.serviceName = serviceName;
    }

    @PostMapping(Endpoints.EVENT_MEMORY_MONITORING)
    public void sendMemoryStatus() {
        log.debug("Reporting memory status...");
        monitoringClient.reportMemoryStatus(memoryStatusModelFactory.create(serviceName), commonConfigProperties.getDefaultLocale());
    }
}
