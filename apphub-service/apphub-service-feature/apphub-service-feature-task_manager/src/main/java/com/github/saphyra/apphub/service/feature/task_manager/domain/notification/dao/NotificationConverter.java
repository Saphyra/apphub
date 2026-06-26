package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationStatus;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class NotificationConverter extends ConverterBase<NotificationEntity, Notification> {
    private final TaskManagerProperties properties;
    private final ObjectMapper objectMapper;
    private final UuidConverter uuidConverter;

    @Override
    protected NotificationEntity processDomainConversion(Notification domain) {
        return NotificationEntity.builder()
            .recipient(uuidConverter.convertDomain(domain.getRecipient()))
            .notificationId(uuidConverter.convertDomain(domain.getNotificationId()))
            .organizationId(uuidConverter.convertDomain(domain.getOrganizationId()))
            .status(domain.getStatus().name())
            .notificationType(domain.getNotificationType().name())
            .createdAt(domain.getCreatedAt())
            .lastModified(domain.getLastModified())
            .expiration(Optional.of(domain.getStatus())
                .filter(status -> status == NotificationStatus.READ)
                .map(_ -> domain.getLastModified().plus(properties.getNotificationExpiration()))
                .orElse(null)
            )
            .data(objectMapper.writeValueAsString(domain.getData()))
            .build();
    }

    @Override
    protected Notification processEntityConversion(NotificationEntity entity) {
        TypeReference<java.util.Map<String, String>> typeRef = new TypeReference<>() {
        };

        return Notification.builder()
            .recipient(uuidConverter.convertEntity(entity.getRecipient()))
            .notificationId(uuidConverter.convertEntity(entity.getNotificationId()))
            .organizationId(uuidConverter.convertEntity(entity.getOrganizationId()))
            .status(NotificationStatus.valueOf(entity.getStatus()))
            .notificationType(NotificationType.valueOf(entity.getNotificationType()))
            .createdAt(entity.getCreatedAt())
            .lastModified(entity.getLastModified())
            .data(objectMapper.readValue(entity.getData(), typeRef))
            .build();
    }
}
