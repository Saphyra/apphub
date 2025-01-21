package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.AdminPanelEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.performance_reporting.PerformanceReportingTopic;
import io.restassured.response.Response;

import java.util.UUID;

public class PerformanceReportingActions {
    public static Response getEnableResponse(int serverPort, UUID accessTokenId, PerformanceReportingTopic topic) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(topic))
            .post(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_ENABLE_TOPIC));
    }

    public static Response getDisableResponse(int serverPort, UUID accessTokenId, PerformanceReportingTopic topic) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(topic))
            .delete(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_DISABLE_TOPIC));
    }

    public static Response getReportsResponse(int serverPort, UUID accessTokenId, PerformanceReportingTopic topic) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_PERFORMANCE_REPORTING_GET_REPORTS, "topic", topic));
    }
}
