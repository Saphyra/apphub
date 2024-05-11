package com.github.saphyra.apphub.api.admin_panel.server;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MonitoringController {
    /**
     * Sending the memory report to the connected clients
     */
    @PutMapping(Endpoints.ADMIN_PANEL_INTERNAL_REPORT_MEMORY_STATUS)
    void reportMemoryStatus(@RequestBody MemoryStatusModel memoryStatus);

    /**
     * Called by scheduler-service. If a client is connected, asking all the other services to report their actual memory status.
     * If no client connected, the request is not forwarded to the services to save resources.
     */
    @PostMapping(Endpoints.EVENT_TRIGGER_MEMORY_STATUS_UPDATE)
    void triggerMemoryStatusUpdate();
}
