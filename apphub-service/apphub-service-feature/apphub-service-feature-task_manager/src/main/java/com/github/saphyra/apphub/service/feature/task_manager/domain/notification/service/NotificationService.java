package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service;

import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.Notification;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationConstants;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationFactory;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
