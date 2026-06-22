package com.github.saphyra.apphub.service.feature.task_manager.domain.notification;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.SetNotificationStatusRequest;
import com.github.saphyra.apphub.api.feature.task_manager.server.NotificationController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service.NotificationQueryService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class NotificationControllerImpl implements NotificationController {
    private final NotificationQueryService notificationQueryService;
    private final NotificationService notificationService;

    @Override
    public List<NotificationResponse> getNotifications(AccessToken accessToken) {
        log.info("{} wants to know their notifications", accessToken.getUserId());

        return notificationQueryService.getNotifications(accessToken.getUserId());
    }

    @Override
    public void setNotificationStatus(SetNotificationStatusRequest request, AccessToken accessToken) {
        log.info("{} wants to set status of notifications {} to {}", accessToken.getUserId(), request.getNotificationIds(), request.getStatus());

        ValidationUtil.notNull(request.getStatus(), "status");
        ValidationUtil.doesNotContainNull(request.getNotificationIds(), "notificationIds");

        notificationService.setStatus(accessToken.getUserId(), request.getNotificationIds(), request.getStatus());
    }

    @Override
    public void deleteNotification(Set<UUID> notificationIds, AccessToken accessToken) {
        log.info("{} wants to delete notifications {}", accessToken.getUserId(), notificationIds);

        notificationService.delete(accessToken.getUserId(), notificationIds);
    }
}
