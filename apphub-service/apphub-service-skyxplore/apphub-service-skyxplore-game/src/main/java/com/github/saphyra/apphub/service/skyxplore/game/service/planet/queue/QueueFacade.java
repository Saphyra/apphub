package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class QueueFacade {
    private final GameDao gameDao;
    private final List<QueueService> services;
    private final QueueItemToResponseConverter converter;

    public List<QueueResponse> getQueueOfPlanet(UUID userId, UUID planetId) {
        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();
        return getQueueOfPlanet(gameData, planetId)
            .stream()
            .map(queueItem -> converter.convert(queueItem, gameData, planetId))
            .collect(Collectors.toList());
    }

    public List<QueueItem> getQueueOfPlanet(GameData gameData, UUID location) {
        return services.stream()
            .flatMap(queueService -> queueService.getQueue(gameData, location).stream())
            .collect(Collectors.toList());
    }

    public void setPriority(UUID userId, UUID planetId, String type, UUID itemId, Integer priority) {
        QueueItemType itemType = ValidationUtil.convertToEnumChecked(type, QueueItemType::valueOf, "type");

        findQueueService(itemType)
            .setPriority(userId, planetId, itemId, priority);
    }

    public void cancelItem(UUID userId, UUID planetId, String type, UUID itemId) {
        QueueItemType itemType = ValidationUtil.convertToEnumChecked(type, QueueItemType::valueOf, "type");

        findQueueService(itemType)
            .cancel(userId, planetId, itemId);
    }

    private QueueService findQueueService(QueueItemType itemType) {
        return services.stream()
            .filter(queueService -> queueService.getType() == itemType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "QueueService not found for type " + itemType));
    }
}
