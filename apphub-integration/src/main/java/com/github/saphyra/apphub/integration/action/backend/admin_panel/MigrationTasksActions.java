package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.AdminPanelEndpoints;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MigrationTasksResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTasksActions {
    public static MigrationTasksResponse findMigrationTaskByEventValidated(int serverPort, UUID accessTokenId, String event) {
        return findMigrationTaskByEvent(serverPort, accessTokenId, event)
            .orElseThrow(() -> new RuntimeException("MigrationTask not found by event " + event));
    }

    public static Optional<MigrationTasksResponse> findMigrationTaskByEvent(int serverPort, UUID accessTokenId, String event) {
        return getMigrationTasks(serverPort, accessTokenId)
            .stream()
            .filter(migrationTasksResponse -> migrationTasksResponse.getEvent().equals(event))
            .findAny();
    }

    private static List<MigrationTasksResponse> getMigrationTasks(int serverPort, UUID accessTokenId) {
        Response response = getGetMigrationTasksResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(MigrationTasksResponse[].class));
    }

    public static Response getGetMigrationTasksResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_MIGRATION_GET_TASKS));
    }

    public static void triggerTask(int serverPort, UUID accessTokenId, String event) {
        Response response = getTriggerTaskResponse(serverPort, accessTokenId, event);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getTriggerTaskResponse(int serverPort, UUID accessTokenId, String event) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_MIGRATION_TRIGGER_TASK, "event", event));
    }

    public static void deleteTask(int serverPort, UUID accessTokenId, String event) {
        Response response = getDeleteTaskResponse(serverPort, accessTokenId, event);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteTaskResponse(int serverPort, UUID accessTokenId, String event) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_MIGRATION_DELETE_TASK, "event", event));
    }
}
