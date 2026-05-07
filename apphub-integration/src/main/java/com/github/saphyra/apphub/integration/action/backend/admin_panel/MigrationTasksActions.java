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
    public static MigrationTasksResponse findMigrationTaskByEventValidated(int serverPort, String accessToken, String event) {
        return findMigrationTaskByEvent(serverPort, accessToken, event)
            .orElseThrow(() -> new RuntimeException("MigrationTask not found by event " + event));
    }

    public static Optional<MigrationTasksResponse> findMigrationTaskByEvent(int serverPort, String accessToken, String event) {
        return getMigrationTasks(serverPort, accessToken)
            .stream()
            .filter(migrationTasksResponse -> migrationTasksResponse.getEvent().equals(event))
            .findAny();
    }

    private static List<MigrationTasksResponse> getMigrationTasks(int serverPort, String accessToken) {
        Response response = getGetMigrationTasksResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(MigrationTasksResponse[].class));
    }

    public static Response getGetMigrationTasksResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_MIGRATION_GET_TASKS));
    }

    public static void triggerTask(int serverPort, String accessToken, String event) {
        Response response = getTriggerTaskResponse(serverPort, accessToken, event);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getTriggerTaskResponse(int serverPort, String accessToken, String event) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_MIGRATION_TRIGGER_TASK, "event", event));
    }

    public static void deleteTask(int serverPort, String accessToken, String event) {
        Response response = getDeleteTaskResponse(serverPort, accessToken, event);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteTaskResponse(int serverPort, String accessToken, String event) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, AdminPanelEndpoints.ADMIN_PANEL_MIGRATION_DELETE_TASK, "event", event));
    }
}
