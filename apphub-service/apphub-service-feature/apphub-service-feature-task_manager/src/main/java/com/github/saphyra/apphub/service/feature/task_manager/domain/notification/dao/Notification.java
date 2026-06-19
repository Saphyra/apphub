package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Notification {
    private final UUID recipient;
    private final UUID notificationId;
    private final NotificationStatus status;
    private final NotificationType notificationType;
    private final LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private Map<String, String> data;
}
