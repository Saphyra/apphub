package com.github.saphyra.apphub.api.feature.task_manager.model.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SetNotificationStatusRequest {
    private NotificationStatus status;
    private Set<UUID> notificationIds;
}
