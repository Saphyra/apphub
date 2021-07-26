package com.github.saphyra.apphub.api.admin_panel.server;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface ErrorReporterController {
    @PutMapping(Endpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR)
    void reportError(@RequestBody ErrorReportModel model);

    @PostMapping(Endpoints.ADMIN_PANEL_GET_ERROR_REPORTS)
    List<ErrorReportOverview> getErrorReports(@RequestBody GetErrorReportsRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.ADMIN_PANEL_GET_ERROR_REPORT)
    ErrorReportModel getErrorReport(@PathVariable("id") UUID id, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
