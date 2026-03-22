package com.github.saphyra.apphub.api.etc.admin_panel.client;

import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.ErrorReport;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.AdminPanelEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "admin-panel-error-reporter", url = "${serviceUrls.adminPanel}")
public interface ErrorReporterClient {
    @PutMapping(AdminPanelEndpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR)
    void reportError(@RequestBody ErrorReport model, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
