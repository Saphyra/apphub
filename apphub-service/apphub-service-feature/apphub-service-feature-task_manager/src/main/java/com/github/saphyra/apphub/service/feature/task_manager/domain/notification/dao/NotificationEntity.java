package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class NotificationEntity {
    private String recipient;
    private String notificationId;
    private String status;
    private String notificationType;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private LocalDateTime expiration;
    private String data;
}
