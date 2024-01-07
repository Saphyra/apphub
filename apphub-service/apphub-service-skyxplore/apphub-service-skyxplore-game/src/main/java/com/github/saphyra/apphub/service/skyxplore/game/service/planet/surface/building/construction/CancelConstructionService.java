package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelConstructionService {
    private final GameDao gameDao;
    private final SyncCacheFactory syncCacheFactory;
    private final AllocationRemovalService allocationRemovalService;

    public void cancelConstructionOfConstruction(UUID userId, UUID constructionId) {
        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Construction construction = gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(construction.getExternalReference());

        processCancellation(game, building, construction);
    }

    public void cancelConstructionOfBuilding(UUID userId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(buildingId);

        Construction construction = game.getData()
            .getConstructions()
            .findByExternalReferenceValidated(building.getBuildingId());

        processCancellation(game, building, construction);
    }

    @SneakyThrows
    private void processCancellation(Game game, Building building, Construction construction) {
        GameData gameData = game.getData();

        SyncCache syncCache = syncCacheFactory.create();
        game.getEventLoop()
            .processWithWait(() -> {
                    gameData.getProcesses()
                        .findByExternalReferenceAndTypeValidated(construction.getConstructionId(), ProcessType.CONSTRUCTION)
                        .cleanup(syncCache);

                    gameData.getConstructions()
                        .remove(construction);

                    allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, construction.getConstructionId());

                    syncCache.deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);

                    if (building.getLevel() == 0) {
                        gameData.getBuildings()
                            .remove(building);
                        syncCache.deleteGameItem(building.getBuildingId(), GameItemType.BUILDING);
                    }
                },
                syncCache
            )
            .getOrThrow();
    }
}
