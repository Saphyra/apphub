package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation.TerraformationProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation.TerraformationProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TerraformationService {
    private final TerraformingPossibilitiesService terraformingPossibilitiesService;
    private final GameDao gameDao;
    private final ResourceAllocationService resourceAllocationService;
    private final ConstructionFactory constructionFactory;
    private final TerraformationProcessFactory terraformationProcessFactory;
    private final SyncCacheFactory syncCacheFactory;

    void terraform(UUID userId, UUID planetId, UUID surfaceId, String surfaceTypeString) {
        SurfaceType surfaceType = ValidationUtil.convertToEnumChecked(surfaceTypeString, SurfaceType::valueOf, "surfaceType");

        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        if (gameData.getConstructions().findByExternalReference(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Terraformation already in progress on surface " + surfaceId);
        }

        if (gameData.getBuildings().findBySurfaceId(surfaceId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("There is already a building on surface " + surfaceId);
        }

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(surfaceId);

        ConstructionRequirements constructionRequirements = terraformingPossibilitiesService.getOptional(surface.getSurfaceType())
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed."))
            .stream()
            .filter(terraformingPossibilities -> terraformingPossibilities.getSurfaceType() == surfaceType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed to " + surfaceType))
            .getConstructionRequirements();

        Construction terraformation = constructionFactory.create(
            surfaceId,
            ConstructionType.TERRAFORMATION,
            planetId,
            constructionRequirements.getParallelWorkers(),
            constructionRequirements.getRequiredWorkPoints(),
            surfaceTypeString
        );

        Planet planet = gameData.getPlanets()
            .get(planetId);

        SyncCache syncCache = syncCacheFactory.create(game);

        game.getEventLoop()
            .processWithWait(() -> {
                resourceAllocationService.processResourceRequirements(
                    syncCache,
                    gameData,
                    planetId,
                    planet.getOwner(),
                    terraformation.getConstructionId(),
                    constructionRequirements.getRequiredResources()
                );

                gameData.getConstructions()
                    .add(terraformation);

                TerraformationProcess terraformationProcess = terraformationProcessFactory.create(gameData, planetId, surface, terraformation);

                gameData.getProcesses()
                    .add(terraformationProcess);

                syncCache.terraformationCreated(userId, planetId, terraformation, surface, terraformationProcess);
            }, syncCache)
            .getOrThrow();
    }
}
