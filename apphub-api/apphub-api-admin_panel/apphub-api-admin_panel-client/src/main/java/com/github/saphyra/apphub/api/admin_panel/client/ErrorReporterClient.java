package com.github.saphyra.apphub.api.admin_panel.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;

@FeignClient(name = "admin-panel-error-reporter", url = "${serviceUrls.adminPanel}")
public interface ErrorReporterClient {
    @PutMapping(Endpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR)
    void reportError(@RequestBody ErrorReport model, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
