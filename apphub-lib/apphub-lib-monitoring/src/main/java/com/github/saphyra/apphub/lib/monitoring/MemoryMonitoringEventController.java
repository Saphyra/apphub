package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.admin_panel.client.MonitoringClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
//TODO unit test
public class MemoryMonitoringEventController {
    private final CommonConfigProperties commonConfigProperties;
    private final MonitoringClient monitoringClient;

    private final String serviceName;

    public MemoryMonitoringEventController(
        CommonConfigProperties commonConfigProperties,
        MonitoringClient monitoringClient,
        @Value("${spring.application.name}") String serviceName
    ) {
        this.commonConfigProperties = commonConfigProperties;
        this.monitoringClient = monitoringClient;
        this.serviceName = serviceName;
    }

    @PostMapping(Endpoints.EVENT_MEMORY_MONITORING)
    public void sendMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();

        MemoryStatusModel model = MemoryStatusModel.builder()
            .service(serviceName)
            .availableMemoryBytes(maxMemory)
            .allocatedMemoryBytes(totalMemory)
            .usedMemoryBytes(totalMemory - freeMemory)
            .build();

        monitoringClient.reportMemoryStatus(model, commonConfigProperties.getDefaultLocale());
    }
}
