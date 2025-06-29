package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.CancelConstructionAreaConstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.util.WorkPointsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructConstructionAreaQueueService implements QueueService {
    private final GameDao gameDao;
    private final ConstructionConverter constructionConverter;
    private final CancelConstructionAreaConstructionService cancelConstructionAreaConstructionService;
    private final WorkPointsUtil workPointsUtil;

    @Override
    public QueueItemType getType() {
        return QueueItemType.CONSTRUCT_CONSTRUCTION_AREA;
    }

    @Override
    public List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getConstructions()
            .stream()
            .filter(construction -> gameData.getConstructionAreas().findByConstructionAreaId(construction.getExternalReference()).isPresent())
            .map(construction -> QueueItem.builder()
                .itemId(construction.getConstructionId())
                .type(getType())
                .requiredWorkPoints(construction.getRequiredWorkPoints())
                .currentWorkPoints(workPointsUtil.getCompletedWorkPoints(gameData, construction.getConstructionId(), ProcessType.CONSTRUCT_CONSTRUCTION_AREA))
                .priority(construction.getPriority())
                .data(Map.of("dataId", gameData.getConstructionAreas().findByIdValidated(construction.getExternalReference()).getDataId()))
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void setPriority(UUID userId, UUID planetId, UUID itemId, Integer priority) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByIdValidated(itemId);

        game.getEventLoop()
            .processWithWait(() -> {
                construction.setPriority(priority);

                game.getProgressDiff()
                    .save(constructionConverter.toModel(gameData.getGameId(), construction));
            })
            .getOrThrow();
    }

    @Override
    public void cancel(UUID userId, UUID planetId, UUID itemId) {
        cancelConstructionAreaConstructionService.cancelConstruction(userId, itemId);
    }
}
