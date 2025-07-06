package com.github.saphyra.apphub.api.admin_panel.server;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportResponse;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.AdminPanelEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface PerformanceReportingController {
    @GetMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_GET_TOPIC_STATUS)
    List<PerformanceReportingTopicStatus> getTopicStatus(@RequestHeader(value = Constants.ACCESS_TOKEN_HEADER, required = false) AccessTokenHeader accessTokenHeader);

    @PostMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_ENABLE_TOPIC)
    List<PerformanceReportingTopicStatus> enableTopic(@RequestBody OneParamRequest<PerformanceReportingTopic> topic, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_DISABLE_TOPIC)
    List<PerformanceReportingTopicStatus> disableTopic(@RequestBody OneParamRequest<PerformanceReportingTopic> topic, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_GET_REPORTS)
    List<PerformanceReportResponse> getReports(@PathVariable("topic") PerformanceReportingTopic topic, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_DELETE_REPORTS)
    void deleteReports(@PathVariable("topic") PerformanceReportingTopic topic, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_REPORT)
    void report(@RequestBody PerformanceReportRequest request);
}
