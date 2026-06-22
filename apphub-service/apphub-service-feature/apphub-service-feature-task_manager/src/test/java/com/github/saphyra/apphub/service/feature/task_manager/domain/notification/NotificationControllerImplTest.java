package com.github.saphyra.apphub.service.feature.task_manager.domain.notification;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.SetNotificationStatusRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service.NotificationQueryService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service.NotificationService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class NotificationControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NOTIFICATION_ID = UUID.randomUUID();

    @Mock
    private NotificationQueryService notificationQueryService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private NotificationResponse notificationResponse;

    @Test
    void getNotifications() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(notificationQueryService.getNotifications(USER_ID)).willReturn(List.of(notificationResponse));

        assertThat(underTest.getNotifications(accessToken)).containsExactly(notificationResponse);
    }

    @Test
    void setNotificationStatus() {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(NotificationStatus.READ)
            .notificationIds(Set.of(NOTIFICATION_ID))
            .build();
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.setNotificationStatus(request, accessToken);

        then(notificationService).should().setStatus(USER_ID, Set.of(NOTIFICATION_ID), NotificationStatus.READ);
    }

    @Test
    void setNotificationStatus_nullStatus() {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(null)
            .notificationIds(Set.of(NOTIFICATION_ID))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.setNotificationStatus(request, accessToken), "status", "must not be null");
    }

    @Test
    void setNotificationStatus_nullNotificationIds() {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(NotificationStatus.READ)
            .notificationIds(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.setNotificationStatus(request, accessToken), "notificationIds", "must not be null");
    }

    @Test
    void setNotificationStatus_notificationIdsContainsNull() {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(NotificationStatus.READ)
            .notificationIds(new HashSet<>(Arrays.asList(NOTIFICATION_ID, null)))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.setNotificationStatus(request, accessToken), "notificationIds", "must not contain null values");
    }

    @Test
    void deleteNotification() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.deleteNotification(Set.of(NOTIFICATION_ID), accessToken);

        then(notificationService).should().delete(USER_ID, Set.of(NOTIFICATION_ID));
    }
}