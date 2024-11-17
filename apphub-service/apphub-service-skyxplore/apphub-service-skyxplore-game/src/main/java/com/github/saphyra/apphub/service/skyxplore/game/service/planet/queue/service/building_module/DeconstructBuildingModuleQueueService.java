package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.building_module;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.CancelDeconstructionOfBuildingModuleService;
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
//TODO unit test
class DeconstructBuildingModuleQueueService implements QueueService {
    private final GameProperties gameProperties;
    private final GameDao gameDao;
    private final DeconstructionConverter deconstructionConverter;
    private final CancelDeconstructionOfBuildingModuleService cancelDeconstructionOfBuildingModuleService;

    @Override
    public QueueItemType getType() {
        return QueueItemType.DECONSTRUCT_BUILDING_MODULE;
    }

    @Override
    public List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getDeconstructions()
            .stream()
            .filter(deconstruction -> gameData.getBuildingModules().findByBuildingModuleId(deconstruction.getExternalReference()).isPresent())
            .map(deconstruction -> QueueItem.builder()
                .itemId(deconstruction.getDeconstructionId())
                .type(getType())
                .requiredWorkPoints(gameProperties.getDeconstruction().getRequiredWorkPoints())
                .currentWorkPoints(deconstruction.getCurrentWorkPoints())
                .priority(deconstruction.getPriority())
                .data(Map.of("dataId", gameData.getBuildingModules().findByBuildingModuleIdValidated(deconstruction.getExternalReference()).getDataId()))
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void setPriority(UUID userId, UUID planetId, UUID itemId, Integer priority) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionIdValidated(itemId);

        game.getEventLoop()
            .processWithWait(() -> {
                deconstruction.setPriority(priority);

                game.getProgressDiff()
                    .save(deconstructionConverter.toModel(gameData.getGameId(), deconstruction));
            })
            .getOrThrow();
    }

    @Override
    public void cancel(UUID userId, UUID planetId, UUID itemId) {
        cancelDeconstructionOfBuildingModuleService.cancelDeconstruction(userId, itemId);
    }
}
