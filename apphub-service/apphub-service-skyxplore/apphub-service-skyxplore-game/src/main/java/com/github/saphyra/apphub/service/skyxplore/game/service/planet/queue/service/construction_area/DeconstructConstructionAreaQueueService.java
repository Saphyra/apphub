package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction_area;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.CancelDeconstructConstructionAreaService;
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
class DeconstructConstructionAreaQueueService implements QueueService {
    private final GameProperties gameProperties;
    private final GameDao gameDao;
    private final DeconstructionConverter deconstructionConverter;
    private final CancelDeconstructConstructionAreaService cancelDeconstructConstructionAreaService;

    @Override
    public QueueItemType getType() {
        return QueueItemType.DECONSTRUCT_CONSTRUCTION_AREA;
    }

    @Override
    public List<QueueItem> getQueue(GameData gameData, UUID location) {
        return gameData.getDeconstructions()
            .stream()
            .filter(deconstruction -> gameData.getConstructionAreas().findByConstructionAreaId(deconstruction.getExternalReference()).isPresent())
            .map(deconstruction -> QueueItem.builder()
                .itemId(deconstruction.getDeconstructionId())
                .type(getType())
                .requiredWorkPoints(gameProperties.getDeconstruction().getRequiredWorkPoints())
                .currentWorkPoints(deconstruction.getCurrentWorkPoints())
                .priority(deconstruction.getPriority())
                .data(Map.of("dataId", gameData.getConstructionAreas().findByConstructionAreaIdValidated(deconstruction.getExternalReference()).getDataId()))
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
        cancelDeconstructConstructionAreaService.cancelDeconstruction(userId, itemId);
    }
}
