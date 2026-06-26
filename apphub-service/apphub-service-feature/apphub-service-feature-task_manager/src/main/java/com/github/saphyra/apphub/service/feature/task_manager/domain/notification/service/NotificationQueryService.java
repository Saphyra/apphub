package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.notification.NotificationResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationConstants;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationQueryService {
    private final NotificationDao notificationDao;
    private final DateTimeUtil dateTimeUtil;
    private final AccountClient accountClient;
    private final UuidConverter uuidConverter;

    public List<NotificationResponse> getNotifications(UUID userId, UUID organizationId) {
        return notificationDao.getByUserIdAndOrganizationId(userId, organizationId)
            .stream()
            .map(notification -> NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .status(notification.getStatus())
                .type(notification.getNotificationType())
                .createdAt(dateTimeUtil.toEpochMillis(notification.getCreatedAt()))
                .data(mapData(notification.getData()))
                .build())
            .toList();
    }

    private Map<String, String> mapData(Map<String, String> data) {
        return data.entrySet()
            .stream()
            .map(e -> new BiWrapper<>(e.getKey(), e.getValue()))
            .map(bw -> mapData(bw.getEntity1(), bw.getEntity2()))
            .flatMap(List::stream)
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));
    }

    private List<BiWrapper<String, String>> mapData(String key, String value) {
        return switch (key) {
            case NotificationConstants.KEY_USER_ID -> {
                AccountResponse account = accountClient.getAccountInternal(uuidConverter.convertEntity(value));
                yield List.of(
                    new BiWrapper<>(NotificationConstants.KEY_USERNAME, account.getUsername()),
                    new BiWrapper<>(NotificationConstants.KEY_EMAIL, account.getEmail())
                );
            }
            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }
}
