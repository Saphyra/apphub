package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class NotificationFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;
    private final UuidConverter uuidConverter;

    public Notification create(UUID recipient, NotificationType notificationType, String key, UUID value) {
        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();

        return Notification.builder()
            .recipient(recipient)
            .notificationId(idGenerator.randomUuid())
            .status(NotificationStatus.UNREAD)
            .notificationType(notificationType)
            .createdAt(currentTime)
            .lastModified(currentTime)
            .data(Map.of(key, uuidConverter.convertDomain(value)))
            .build();
    }
}
