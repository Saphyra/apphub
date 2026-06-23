package com.github.saphyra.apphub.api.feature.task_manager.server;

import com.github.saphyra.apphub.api.feature.task_manager.model.TaskManagerEndpoints;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.SetNotificationStatusRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Set;
import java.util.UUID;

//TODO API test
//TODO role protection test
public interface NotificationController {
    @GetMapping(TaskManagerEndpoints.TASK_MANAGER_GET_NOTIFICATIONS)
    List<NotificationResponse> getNotifications(@PathVariable("organizationId") UUID organizationId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @PostMapping(TaskManagerEndpoints.TASK_MANAGER_SET_NOTIFICATION_STATUS)
    void setNotificationStatus(@RequestBody SetNotificationStatusRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @DeleteMapping(TaskManagerEndpoints.TASK_MANAGER_DELETE_NOTIFICATION)
    void deleteNotification(@RequestBody Set<UUID> notificationIds, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}
