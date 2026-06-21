package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationConverterTest {
    private static final UUID RECIPIENT = UUID.randomUUID();
    private static final UUID NOTIFICATION_ID = UUID.randomUUID();
    private static final NotificationStatus STATUS = NotificationStatus.READ;
    private static final NotificationType TYPE = NotificationType.USER_ACCEPTED_YOUR_INVITATION;
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime LAST_MODIFIED = CREATED_AT.plusSeconds(3600);
    private static final String DATA_JSON = "{\"key\":\"value\"}";
    private static final Map<String, String> DATA = Map.of("key", "value");
    private static final String RECIPIENT_UUID = "recipient-uuid";
    private static final String NOTIFICATION_UUID = "notification-uuid";
    private static final Duration EXPIRATION = Duration.ofSeconds(32);

    @Mock
    private TaskManagerProperties properties;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationConverter converter;

    @Test
    void convertDomain_read() {
        Notification domain = Notification.builder()
            .recipient(RECIPIENT)
            .notificationId(NOTIFICATION_ID)
            .status(STATUS)
            .notificationType(TYPE)
            .createdAt(CREATED_AT)
            .lastModified(LAST_MODIFIED)
            .data(DATA)
            .build();

        given(uuidConverter.convertDomain(RECIPIENT)).willReturn(RECIPIENT_UUID);
        given(uuidConverter.convertDomain(NOTIFICATION_ID)).willReturn(NOTIFICATION_UUID);
        given(objectMapper.writeValueAsString(DATA)).willReturn(DATA_JSON);
        given(properties.getNotificationExpiration()).willReturn(EXPIRATION);

        NotificationEntity result = converter.processDomainConversion(domain);

        assertThat(result.getRecipient()).isEqualTo(RECIPIENT_UUID);
        assertThat(result.getNotificationId()).isEqualTo(NOTIFICATION_UUID);
        assertThat(result.getStatus()).isEqualTo(STATUS.name());
        assertThat(result.getNotificationType()).isEqualTo(TYPE.name());
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getLastModified()).isEqualTo(LAST_MODIFIED);
        assertThat(result.getData()).isEqualTo(DATA_JSON);
        assertThat(result.getExpiration()).isEqualTo(LAST_MODIFIED.plus(EXPIRATION));
    }

    @Test
    void convertDomain_unread() {
        Notification domain = Notification.builder()
            .recipient(RECIPIENT)
            .notificationId(NOTIFICATION_ID)
            .status(NotificationStatus.UNREAD)
            .notificationType(TYPE)
            .createdAt(CREATED_AT)
            .lastModified(LAST_MODIFIED)
            .data(DATA)
            .build();

        given(uuidConverter.convertDomain(RECIPIENT)).willReturn(RECIPIENT_UUID);
        given(uuidConverter.convertDomain(NOTIFICATION_ID)).willReturn(NOTIFICATION_UUID);
        given(objectMapper.writeValueAsString(DATA)).willReturn(DATA_JSON);

        NotificationEntity result = converter.processDomainConversion(domain);

        assertThat(result.getRecipient()).isEqualTo(RECIPIENT_UUID);
        assertThat(result.getNotificationId()).isEqualTo(NOTIFICATION_UUID);
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.UNREAD.name());
        assertThat(result.getNotificationType()).isEqualTo(TYPE.name());
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getLastModified()).isEqualTo(LAST_MODIFIED);
        assertThat(result.getData()).isEqualTo(DATA_JSON);
        assertThat(result.getExpiration()).isNull();
    }

    @Test
    void convertEntity() {
        NotificationEntity entity = NotificationEntity.builder()
            .recipient(RECIPIENT_UUID)
            .notificationId(NOTIFICATION_UUID)
            .status(STATUS.name())
            .notificationType(TYPE.name())
            .createdAt(CREATED_AT)
            .lastModified(LAST_MODIFIED)
            .data(DATA_JSON)
            .build();

        given(uuidConverter.convertEntity(RECIPIENT_UUID)).willReturn(RECIPIENT);
        given(uuidConverter.convertEntity(NOTIFICATION_UUID)).willReturn(NOTIFICATION_ID);
        given(objectMapper.readValue(eq(DATA_JSON), any(TypeReference.class))).willReturn(DATA);

        Notification result = converter.processEntityConversion(entity);

        assertThat(result.getRecipient()).isEqualTo(RECIPIENT);
        assertThat(result.getNotificationId()).isEqualTo(NOTIFICATION_ID);
        assertThat(result.getStatus()).isEqualTo(STATUS);
        assertThat(result.getNotificationType()).isEqualTo(TYPE);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getLastModified()).isEqualTo(LAST_MODIFIED);
        assertThat(result.getData()).containsEntry("key", "value");
    }
}