package com.github.saphyra.apphub.integration.action.backend.task_manager;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.TaskManagerEndpoints;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.OrganizationResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerOrganizationActions {
    public static Response createOrganizationResponse(int serverPort, String accessToken, CreateOrganizationRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_CREATE_ORGANIZATION));
    }

    public static Response getOrganizationsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_GET_ORGANIZATIONS));
    }

    public static Response getOrganizationResponse(int serverPort, String accessToken, UUID organizationId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_GET_ORGANIZATION, "organizationId", organizationId));
    }

    public static void createOrganization(int serverPort, String accessToken, CreateOrganizationRequest request) {
        Response response = createOrganizationResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<OrganizationResponse> getOrganizations(int serverPort, String accessToken) {
        Response response = getOrganizationsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.as(OrganizationResponse[].class));
    }

    public static OrganizationResponse getOrganization(int serverPort, String accessToken, UUID organizationId) {
        Response response = getOrganizationResponse(serverPort, accessToken, organizationId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.as(OrganizationResponse.class);
    }
}

