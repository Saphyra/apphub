package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MigrationTasksResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTasksActions {
    public static MigrationTasksResponse findMigrationTaskByEventValidated(UUID accessTokenId, String event) {
        return findMigrationTaskByEvent(accessTokenId, event)
            .orElseThrow(() -> new RuntimeException("MigrationTask not found by event " + event));
    }

    public static Optional<MigrationTasksResponse> findMigrationTaskByEvent(UUID accessTokenId, String event) {
        return getMigrationTasks(accessTokenId)
            .stream()
            .filter(migrationTasksResponse -> migrationTasksResponse.getEvent().equals(event))
            .findAny();
    }

    private static List<MigrationTasksResponse> getMigrationTasks(UUID accessTokenId) {
        Response response = getGetMigrationTasksResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(MigrationTasksResponse[].class));
    }

    public static Response getGetMigrationTasksResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.ADMIN_PANEL_MIGRATION_GET_TASKS));
    }

    public static void triggerTask(UUID accessTokenId, String event) {
        Response response = getTriggerTaskResponse(accessTokenId, event);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getTriggerTaskResponse(UUID accessTokenId, String event) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.ADMIN_PANEL_MIGRATION_TRIGGER_TASK, "event", event));
    }

    public static void deleteTask(UUID accessTokenId, String event) {
        Response response = getDeleteTaskResponse(accessTokenId, event);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteTaskResponse(UUID accessTokenId, String event) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.ADMIN_PANEL_MIGRATION_DELETE_TASK, "event", event));
    }
}
