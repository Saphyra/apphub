package com.github.saphyra.apphub.api.admin_panel.client;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("admin-panel-monitoring")
public interface MonitoringClient {
    @PutMapping(Endpoints.ADMIN_PANEL_REPORT_MEMORY_STATUS)
    void reportMemoryStatus(@RequestBody MemoryStatusModel memoryStatus, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.EVENT_TRIGGER_MEMORY_STATUS_UPDATE)
    void triggerMemoryStatusUpdate(@RequestHeader(Constants.LOCALE_HEADER) String locale);
}
