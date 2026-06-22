package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.Notification;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationConstants;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NOTIFICATION_ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final Long CREATED_AT_MILLIS = 23L;
    private static final String VALUE_STRING = "value";
    private static final UUID VALUE = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    @Mock
    private NotificationDao notificationDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private AccountClient accountClient;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private NotificationQueryService underTest;

    @Mock
    private Notification notification;

    @Mock
    private AccountResponse accountResponse;

    @Test
    void getNotifications(){
        given(notificationDao.getByUserId(USER_ID)).willReturn(List.of(notification));
        given(notification.getNotificationId()).willReturn(NOTIFICATION_ID);
        given(notification.getStatus()).willReturn(NotificationStatus.UNREAD);
        given(notification.getNotificationType()).willReturn(NotificationType.USER_ACCEPTED_YOUR_INVITATION);
        given(notification.getCreatedAt()).willReturn(CREATED_AT);
        given(dateTimeUtil.toEpochMillis(CREATED_AT)).willReturn(CREATED_AT_MILLIS);
        given(notification.getData()).willReturn(Map.of(NotificationConstants.KEY_USER_ID, VALUE_STRING));
        given(uuidConverter.convertEntity(VALUE_STRING)).willReturn(VALUE);
        given(accountClient.getAccountInternal(VALUE)).willReturn(accountResponse);
        given(accountResponse.getUsername()).willReturn(USERNAME);
        given(accountResponse.getEmail()).willReturn(EMAIL);

        assertThat(underTest.getNotifications(USER_ID))
            .singleElement()
            .returns(NOTIFICATION_ID, NotificationResponse::getNotificationId)
            .returns(NotificationStatus.UNREAD, NotificationResponse::getStatus)
            .returns(NotificationType.USER_ACCEPTED_YOUR_INVITATION, NotificationResponse::getType)
            .returns(CREATED_AT_MILLIS, NotificationResponse::getCreatedAt)
            .returns(Map.of(NotificationConstants.KEY_USERNAME, USERNAME, NotificationConstants.KEY_EMAIL, EMAIL), NotificationResponse::getData);
    }
}