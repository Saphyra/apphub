package com.github.saphyra.apphub.integration.structure.api.task_manager.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NotificationResponse {
    private UUID notificationId;
    private NotificationStatus status;
    private NotificationType type;
    private Long createdAt;
    private Map<String, String> data;
}
