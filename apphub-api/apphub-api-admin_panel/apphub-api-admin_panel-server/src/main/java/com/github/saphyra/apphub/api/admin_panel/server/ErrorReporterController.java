package com.github.saphyra.apphub.api.admin_panel.server;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.config.Endpoints;

public interface ErrorReporterController {
    @PutMapping(Endpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR)
    void reportError(@RequestBody ErrorReportModel model);
}
