package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.Notification;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationConstants;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    private static final UUID RECIPIENT = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NOTIFICATION_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private NotificationFactory notificationFactory;

    @Mock
    private NotificationDao notificationDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private NotificationService underTest;

    @Mock
    private Notification notification;

    @Test
    void createUserAcceptedYourInvitationNotification() {
        given(notificationFactory.create(RECIPIENT, ORGANIZATION_ID, NotificationType.USER_ACCEPTED_YOUR_INVITATION, NotificationConstants.KEY_USER_ID, USER_ID)).willReturn(notification);

        underTest.createUserAcceptedYourInvitationNotification(List.of(RECIPIENT), ORGANIZATION_ID, USER_ID);

        then(notificationDao).should().save(List.of(notification));
    }

    @Test
    void createUserRejectedYourInvitationNotification() {
        given(notificationFactory.create(RECIPIENT, ORGANIZATION_ID, NotificationType.USER_REJECTED_YOUR_INVITATION, NotificationConstants.KEY_USER_ID, USER_ID)).willReturn(notification);

        underTest.createUserRejectedYourInvitationNotification(List.of(RECIPIENT), ORGANIZATION_ID, USER_ID);

        then(notificationDao).should().save(List.of(notification));
    }

    @Test
    void setStatus() {
        given(notificationDao.getByIds(USER_ID, List.of(NOTIFICATION_ID))).willReturn(List.of(notification));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.setStatus(USER_ID, List.of(NOTIFICATION_ID), NotificationStatus.UNREAD);

        then(notification).should().setStatus(NotificationStatus.UNREAD);
        then(notification).should().setLastModified(CURRENT_TIME);
        then(notificationDao).should().save(List.of(notification));
    }

    @Test
    void delete() {
        underTest.delete(USER_ID, List.of(NOTIFICATION_ID));

        then(notificationDao).should().delete(USER_ID, List.of(NOTIFICATION_ID));
    }
}