package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class NotificationDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NOTIFICATION_ID_1 = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id-string";
    private static final String NOTIFICATION_ID_1_STRING = "notification-id-1";
    private static final String ORGANIZATION_ID_STRING = "organization-id";
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private Notification notification;

    @Mock
    private NotificationEntity entity;

    @InjectMocks
    private NotificationDao underTest;

    @Test
    void save() {
        given(converter.convertDomain(List.of(notification))).willReturn(List.of(entity));

        underTest.save(notification);
    }

    @Test
    void getByUserId() {
        List<NotificationEntity> entities = List.of(entity);
        List<Notification> expectedNotifications = List.of(notification);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(entities);
        given(converter.convertEntity(entities)).willReturn(expectedNotifications);

        List<Notification> result = underTest.getByUserId(USER_ID);

        assertThat(result).isEqualTo(expectedNotifications);
    }

    @Test
    void getByIds() {
        Collection<UUID> notificationIds = List.of(NOTIFICATION_ID_1);
        List<NotificationEntity> entities = List.of(entity);
        List<Notification> expectedNotifications = List.of(notification);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(NOTIFICATION_ID_1)).willReturn(NOTIFICATION_ID_1_STRING);
        given(repository.getByIds(List.of(
            new BiWrapper<>(USER_ID_STRING, NOTIFICATION_ID_1_STRING)
        ))).willReturn(entities);
        given(converter.convertEntity(entities)).willReturn(expectedNotifications);

        List<Notification> result = underTest.getByIds(USER_ID, notificationIds);

        assertThat(result).isEqualTo(expectedNotifications);
    }

    @Test
    void getByIds_emptyNotificationIds() {
        Collection<UUID> notificationIds = List.of();

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByIds(List.of())).willReturn(List.of());
        given(converter.convertEntity(List.of())).willReturn(List.of());

        List<Notification> result = underTest.getByIds(USER_ID, notificationIds);

        assertThat(result).isEmpty();
    }

    @Test
    void delete() {
        Collection<UUID> notificationIds = List.of(NOTIFICATION_ID_1);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(NOTIFICATION_ID_1)).willReturn(NOTIFICATION_ID_1_STRING);

        underTest.delete(USER_ID, notificationIds);
    }

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(entity.getRecipient()).willReturn(USER_ID_STRING);
        given(entity.getNotificationId()).willReturn(NOTIFICATION_ID_1_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().delete(List.of(new BiWrapper<>(USER_ID_STRING, NOTIFICATION_ID_1_STRING)));
    }

    @Test
    void getByUserIdAndOrganizationId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(NOTIFICATION_ID_1)).willReturn(NOTIFICATION_ID_1_STRING);
        given(repository.getByUserIdAndOrganizationId(USER_ID_STRING, NOTIFICATION_ID_1_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(notification));

        assertThat(underTest.getByUserIdAndOrganizationId(USER_ID, NOTIFICATION_ID_1)).containsExactly(notification);
    }

    @Test
    void deleteByOrganizationId() {
        given(uuidConverter.convertDomain(ORGANIZATION_ID)).willReturn(ORGANIZATION_ID_STRING);
        given(repository.getByOrganizationId(ORGANIZATION_ID_STRING)).willReturn(List.of(entity));
        given(entity.getRecipient()).willReturn(USER_ID_STRING);
        given(entity.getNotificationId()).willReturn(NOTIFICATION_ID_1_STRING);

        underTest.deleteByOrganizationId(ORGANIZATION_ID);

        then(repository).should().delete(List.of(new BiWrapper<>(USER_ID_STRING, NOTIFICATION_ID_1_STRING)));
    }
}