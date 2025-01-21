package com.github.saphyra.apphub.api.admin_panel.client;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportRequest;
import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopicStatus;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.AdminPanelEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "admin-panel-performance-reporting", url = "${serviceUrls.adminPanel}")
public interface PerformanceReportingClient {
    @GetMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_GET_TOPIC_STATUS)
    List<PerformanceReportingTopicStatus> getTopicStatus(@RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PutMapping(AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_REPORT)
    void report(@RequestBody PerformanceReportRequest request, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
