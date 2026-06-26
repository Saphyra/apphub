package com.github.saphyra.apphub.integration.action.backend.task_manager;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.TaskManagerEndpoints;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.NotificationResponse;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.SetNotificationStatusRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerNotificationActions {
    public static Response getNotificationsResponse(int serverPort, String accessToken, UUID organizationId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_GET_NOTIFICATIONS, "organizationId", organizationId));
    }

    public static Response setNotificationStatusResponse(int serverPort, String accessToken, SetNotificationStatusRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_SET_NOTIFICATION_STATUS));
    }

    public static Response deleteNotificationsResponse(int serverPort, String accessToken, List<UUID> notificationIds) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(notificationIds)
            .delete(UrlFactory.create(serverPort, TaskManagerEndpoints.TASK_MANAGER_DELETE_NOTIFICATION));
    }

    public static List<NotificationResponse> getNotifications(int serverPort, String accessToken, UUID organizationId) {
        Response response = getNotificationsResponse(serverPort, accessToken, organizationId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotificationResponse[].class));
    }

    public static void deleteNotification(int serverPort, String accessToken1, Set<UUID> notificationIds) {
        Response response = deleteNotificationsResponse(serverPort, accessToken1, List.copyOf(notificationIds));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
