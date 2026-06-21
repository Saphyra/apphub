package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_CREATED_AT;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_DATA;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_EXPIRATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_LAST_MODIFIED;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_NOTIFICATION_TYPE;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_NOTIFICATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_USER;

@Component
@RequiredArgsConstructor
class NotificationMapper extends ConverterBase<Map<String, AttributeValue>, NotificationEntity> {
    private final DateTimeUtil dateTimeUtil;

    @Override
    protected Map<String, AttributeValue> processDomainConversion(NotificationEntity domain) {
        Map<String, AttributeValue> result = new HashMap<>();

        result.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + domain.getRecipient()).build());
        result.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_NOTIFICATION + domain.getNotificationId()).build());
        result.put(COLUMN_STATUS, AttributeValue.builder().s(domain.getStatus()).build());
        result.put(COLUMN_NOTIFICATION_TYPE, AttributeValue.builder().s(domain.getNotificationType()).build());
        result.put(COLUMN_CREATED_AT, AttributeValue.builder().n(String.valueOf(dateTimeUtil.toEpochSecond(domain.getCreatedAt()))).build());
        result.put(COLUMN_LAST_MODIFIED, AttributeValue.builder().n(String.valueOf(dateTimeUtil.toEpochSecond(domain.getLastModified()))).build());
        result.put(COLUMN_DATA, AttributeValue.builder().s(domain.getData()).build());

        Optional.ofNullable(domain.getExpiration())
            .map(dateTimeUtil::toEpochSecond)
            .ifPresent(expiration -> result.put(COLUMN_EXPIRATION, AttributeValue.builder().n(String.valueOf(expiration)).build()));

        return result;
    }

    @Override
    protected NotificationEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return NotificationEntity.builder()
            .recipient(entity.get(COLUMN_PK).s().substring(PREFIX_USER.length()))
            .notificationId(entity.get(COLUMN_SK).s().substring(PREFIX_NOTIFICATION.length()))
            .status(entity.get(COLUMN_STATUS).s())
            .notificationType(entity.get(COLUMN_NOTIFICATION_TYPE).s())
            .createdAt(dateTimeUtil.fromEpochSecond(Long.parseLong(entity.get(COLUMN_CREATED_AT).n())))
            .lastModified(dateTimeUtil.fromEpochSecond(Long.parseLong(entity.get(COLUMN_LAST_MODIFIED).n())))
            .expiration(Optional.ofNullable(entity.get(COLUMN_EXPIRATION)).map(AttributeValue::n).map(Long::parseLong).map(dateTimeUtil::fromEpochSecond).orElse(null))
            .data(entity.get(COLUMN_DATA).s())
            .build();
    }
}
