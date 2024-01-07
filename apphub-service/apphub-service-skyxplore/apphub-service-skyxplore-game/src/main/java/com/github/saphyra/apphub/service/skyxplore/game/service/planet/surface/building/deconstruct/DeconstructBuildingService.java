package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructBuildingService {
    private final GameDao gameDao;
    private final DeconstructionFactory deconstructionFactory;
    private final DeconstructionProcessFactory deconstructionProcessFactory;
    private final SyncCacheFactory syncCacheFactory;
    private final DeconstructionPreconditions deconstructionPreconditions;
    private final DeconstructionConverter deconstructionConverter;

    public void deconstructBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        if (game.getData().getConstructions().findByExternalReference(buildingId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation(buildingId + " is under construction");
        }

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(buildingId);

        deconstructionPreconditions.checkIfBuildingCanBeDeconstructed(game.getData(), building);

        SyncCache syncCache = syncCacheFactory.create();

        Deconstruction deconstruction = deconstructionFactory.create(buildingId, planetId);

        game.getEventLoop()
            .processWithWait(() -> {
                game.getData()
                    .getDeconstructions()
                    .add(deconstruction);

                DeconstructionProcess process = deconstructionProcessFactory.create(game.getData(), planetId, deconstruction.getDeconstructionId());

                syncCache.saveGameItem(process.toModel());
                syncCache.saveGameItem(deconstructionConverter.toModel(game.getGameId(), deconstruction));

                game.getData()
                    .getProcesses()
                    .add(process);
            }, syncCache)
            .getOrThrow();
    }
}
