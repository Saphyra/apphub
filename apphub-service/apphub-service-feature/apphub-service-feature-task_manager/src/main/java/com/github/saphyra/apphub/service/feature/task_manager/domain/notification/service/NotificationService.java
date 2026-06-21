package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.Notification;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationConstants;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class NotificationService {
    private final NotificationFactory notificationFactory;
    private final NotificationDao notificationDao;

    public void createUserAcceptedYourInvitationNotification(List<UUID> recipients, UUID invitedUserId) {
        List<Notification> notifications = recipients.stream()
            .map(recipient -> notificationFactory.create(recipient, NotificationType.USER_ACCEPTED_YOUR_INVITATION, NotificationConstants.KEY_USER_ID, invitedUserId))
            .toList();

        notificationDao.save(notifications);
    }

    public void createUserRejectedYourInvitationNotification(List<UUID> recipients, UUID invitedUserId) {
        List<Notification> notifications = recipients.stream()
            .map(recipient -> notificationFactory.create(recipient, NotificationType.USER_REJECTED_YOUR_INVITATION, NotificationConstants.KEY_USER_ID, invitedUserId))
            .toList();

        notificationDao.save(notifications);
    }

    public void setStatus(UUID userId, Collection<UUID> notificationIds, NotificationStatus status) {
        List<Notification> notifications = notificationDao.getByIds(userId, notificationIds);

        notifications.forEach(notification -> notification.setStatus(status));

        notificationDao.save(notifications);
    }

    public void delete(UUID userId, Collection<UUID> notificationIds) {
        notificationDao.delete(userId, notificationIds);
    }
}
