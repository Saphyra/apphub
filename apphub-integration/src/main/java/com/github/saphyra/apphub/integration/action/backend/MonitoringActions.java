package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.MonitoringEndpoints;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import com.github.saphyra.apphub.integration.structure.api.monitoring.GetMetricsResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitoringActions {
    public static List<Feature> getFeatures(int serverPort, String accessToken) {
        Response response = getGetFeaturesResponse(serverPort, accessToken);

        assertThat(response.statusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(Feature[].class));
    }

    public static Response getGetFeaturesResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, MonitoringEndpoints.MONITORING_GET_FEATURES));
    }

    public static List<String> getFunctionalities(int serverPort, String accessToken, Feature feature) {
        Response response = getGetFunctionalitiesResponse(serverPort, accessToken, feature);

        assertThat(response.statusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(String[].class));
    }

    public static Response getGetFunctionalitiesResponse(int serverPort, String accessToken, Feature feature) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, MonitoringEndpoints.MONITORING_GET_FUNCTIONALITIES, "feature", feature));
    }

    public static List<String> getServices(int serverPort, String accessToken, Feature feature, String functionality) {
        Response response = getGetServicesResponse(serverPort, accessToken, feature, functionality);

        assertThat(response.statusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(String[].class));
    }

    public static Response getGetServicesResponse(int serverPort, String accessToken, Feature feature, String functionality) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, MonitoringEndpoints.MONITORING_GET_SERVICES, Map.of("feature", feature), Map.of("functionality", functionality)));
    }

    public static List<GetMetricsResponse> getMetrics(int serverPort, String accessToken, String metricType, Feature feature, String functionality, String service) {
        Response response = getGetMetricsResponse(serverPort, accessToken, metricType, feature, functionality, service);

        assertThat(response.statusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GetMetricsResponse[].class));
    }

    public static Response getGetMetricsResponse(int serverPort, String accessToken, String type, Feature feature, String functionality, String service) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(
                serverPort,
                MonitoringEndpoints.MONITORING_GET_METRICS,
                Map.of("type", type),
                Map.of("feature", feature, "functionality", functionality, "service", service)
            ));
    }
}
