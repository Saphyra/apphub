package com.github.saphyra.apphub.api.admin_panel.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;

@FeignClient("admin-panel-error-reporter")
public interface ErrorReporterClient {
    @PutMapping(Endpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR)
    void reportError(@RequestBody ErrorReportModel model, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
