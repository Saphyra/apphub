package com.github.saphyra.apphub.api.admin_panel.server;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportResponse;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.AdminPanelEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface ErrorReporterController {
    @PutMapping(AdminPanelEndpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR)
    void reportError(@RequestBody ErrorReport model);

    @PostMapping(AdminPanelEndpoints.ADMIN_PANEL_GET_ERROR_REPORTS)
    List<ErrorReportOverview> getErrorReports(@RequestBody GetErrorReportsRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(AdminPanelEndpoints.ADMIN_PANEL_DELETE_ERROR_REPORTS)
    void deleteErrorReports(@RequestBody List<UUID> ids, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(AdminPanelEndpoints.ADMIN_PANEL_MARK_ERROR_REPORTS)
    void markErrorReports(@RequestBody List<UUID> ids, @PathVariable("status") String status, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(AdminPanelEndpoints.ADMIN_PANEL_GET_ERROR_REPORT)
    ErrorReportResponse getErrorReport(@PathVariable("id") UUID id, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(AdminPanelEndpoints.ADMIN_PANEL_DELETE_READ_ERROR_REPORTS)
    void deleteReadErrorReports(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(AdminPanelEndpoints.ADMIN_PANEL_ERROR_REPORT_DELETE_ALL)
    void deleteAll(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
