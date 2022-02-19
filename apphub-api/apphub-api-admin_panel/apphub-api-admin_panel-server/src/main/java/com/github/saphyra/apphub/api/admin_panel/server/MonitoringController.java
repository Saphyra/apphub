package com.github.saphyra.apphub.api.admin_panel.server;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MonitoringController {
    @PutMapping(Endpoints.ADMIN_PANEL_REPORT_MEMORY_STATUS)
    void reportMemoryStatus(@RequestBody MemoryStatusModel memoryStatus);

    @PostMapping(Endpoints.EVENT_TRIGGER_MEMORY_STATUS_UPDATE)
    void triggerMemoryStatusUpdate();
}
