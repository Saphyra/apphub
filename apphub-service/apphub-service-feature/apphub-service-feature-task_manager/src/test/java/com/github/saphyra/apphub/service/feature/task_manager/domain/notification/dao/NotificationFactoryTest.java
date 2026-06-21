package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationFactoryTest {
    private static final UUID RECIPIENT = UUID.randomUUID();
    private static final UUID VALUE = UUID.randomUUID();
    private static final String KEY = "key";
    private static final UUID NOTIFICATION_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String VALUE_STRING = "value";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private NotificationFactory underTest;

    @Test
    void create() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(uuidConverter.convertDomain(VALUE)).willReturn(VALUE_STRING);

        given(idGenerator.randomUuid()).willReturn(NOTIFICATION_ID);

        assertThat(underTest.create(RECIPIENT, NotificationType.USER_ACCEPTED_YOUR_INVITATION, KEY, VALUE))
            .returns(RECIPIENT, Notification::getRecipient)
            .returns(NOTIFICATION_ID, Notification::getNotificationId)
            .returns(NotificationStatus.UNREAD, Notification::getStatus)
            .returns(NotificationType.USER_ACCEPTED_YOUR_INVITATION, Notification::getNotificationType)
            .returns(CURRENT_TIME, Notification::getCreatedAt)
            .returns(CURRENT_TIME, Notification::getLastModified)
            .returns(Map.of(KEY, VALUE_STRING), Notification::getData);
    }
}