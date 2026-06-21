package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationDao {
    private final NotificationRepository repository;
    private final NotificationConverter converter;
    private final UuidConverter uuidConverter;

    public void save(List<Notification> notifications) {
        repository.save(converter.convertDomain(notifications));
    }

    public List<Notification> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void save(Notification notification) {
        save(List.of(notification));
    }

    public List<Notification> getByIds(UUID userId, Collection<UUID> notificationIds) {
        String userIdString = uuidConverter.convertDomain(userId);

        List<BiWrapper<String, String>> ids = notificationIds.stream()
            .map(notificationId -> new BiWrapper<>(userIdString, uuidConverter.convertDomain(notificationId)))
            .toList();

        return converter.convertEntity(repository.getByIds(ids));
    }

    public void delete(UUID userId, Collection<UUID> notificationIds) {
        String userIdString = uuidConverter.convertDomain(userId);

        List<BiWrapper<String, String>> ids = notificationIds.stream()
            .map(notificationId -> new BiWrapper<>(userIdString, uuidConverter.convertDomain(notificationId)))
            .toList();

        repository.delete(ids);
    }
}
