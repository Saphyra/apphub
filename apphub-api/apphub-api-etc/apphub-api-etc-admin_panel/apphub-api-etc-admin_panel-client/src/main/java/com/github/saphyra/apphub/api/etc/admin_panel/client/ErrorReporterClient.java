package com.github.saphyra.apphub.api.etc.admin_panel.client;

import com.github.saphyra.apphub.api.etc.admin_panel.model.model.AdminPanelEndpoints;
import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.ErrorReport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PutExchange;

public interface ErrorReporterClient {
    @PutExchange(AdminPanelEndpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR)
    void reportError(@RequestBody ErrorReport model);
}
