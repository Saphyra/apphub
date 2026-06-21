package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class NotificationDao {
    private final List<Notification> repository = Collections.synchronizedList(new ArrayList<>());

    public void save(List<Notification> notifications) {
        notifications.stream()
            .filter(notification -> !repository.contains(notification))
            .forEach(repository::add);
    }

    public List<Notification> getByUserId(UUID userId) {
        return repository.stream()
            .filter(notification -> notification.getRecipient().equals(userId))
            .toList();
    }

    public void save(Notification notification) {
        if (!repository.contains(notification)) {
            repository.add(notification);
        }
    }

    public Notification findByIdValidated(UUID userId, UUID notificationId) {
        return repository.stream()
            .filter(notification -> notification.getNotificationId().equals(notificationId))
            .filter(notification -> notification.getRecipient().equals(userId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notFound("Notification not found by id " + notificationId));
    }

    public void delete(UUID userId, UUID notificationId) {
        Notification notification = findByIdValidated(userId, notificationId);

        repository.remove(notification);
    }

    public List<Notification> getByIds(UUID userId, Collection<UUID> notificationIds) {
        return repository.stream()
            .filter(notification -> notificationIds.contains(notification.getNotificationId()))
            .filter(notification -> notification.getRecipient().equals(userId))
            .toList();
    }

    public void delete(UUID userId, Collection<UUID> notificationIds) {
        List<Notification> notifications = getByIds(userId, notificationIds);

        repository.removeAll(notifications);
    }
}
