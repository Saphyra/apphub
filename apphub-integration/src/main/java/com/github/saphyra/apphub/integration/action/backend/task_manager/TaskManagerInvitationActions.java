package com.github.saphyra.apphub.integration.action.backend.task_manager;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.TaskManagerEndpoints;
import com.github.saphyra.apphub.integration.structure.api.task_manager.invitation.InvitationResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerInvitationActions {
    public static Response getInvitationsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_GET_INVITATIONS));
    }

    public static Response acceptInvitationResponse(int serverPort, String accessToken, UUID organizationId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_ACCEPT_INVITATION, "organizationId", organizationId));
    }

    public static Response rejectInvitationResponse(int serverPort, String accessToken, UUID organizationId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_REJECT_INVITATION, "organizationId", organizationId));
    }

    public static List<InvitationResponse> getInvitations(int serverPort, String accessToken) {
        Response response = getInvitationsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(InvitationResponse[].class));
    }

    public static void acceptInvitation(int serverPort, String accessToken2, UUID organizationId) {
        Response response =  acceptInvitationResponse(serverPort, accessToken2, organizationId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void rejectInvitation(int serverPort, String accessToken2, UUID organizationId) {
        Response response =  rejectInvitationResponse(serverPort, accessToken2, organizationId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
