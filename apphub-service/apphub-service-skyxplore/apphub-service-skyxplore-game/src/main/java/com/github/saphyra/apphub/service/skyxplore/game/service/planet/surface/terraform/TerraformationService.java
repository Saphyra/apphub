package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation.TerraformationProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation.TerraformationProcessFactory;
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
    private final ConstructionFactory constructionFactory;
    private final TerraformationProcessFactory terraformationProcessFactory;
    private final ConstructionConverter constructionConverter;
    private final ReservedStorageFactory reservedStorageFactory;

    void terraform(UUID userId, UUID planetId, UUID surfaceId, String surfaceTypeString) {
        SurfaceType surfaceType = ValidationUtil.convertToEnumChecked(surfaceTypeString, SurfaceType::valueOf, "surfaceType");

        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        if (!userId.equals(gameData.getPlanets().findByIdValidated(planetId).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot terraform on planet " + planetId);
        }

        if (gameData.getConstructions().findByExternalReference(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Terraformation already in progress on surface " + surfaceId);
        }

        if (gameData.getConstructionAreas().findBySurfaceId(surfaceId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("There is already a building on surface " + surfaceId);
        }

        if (gameData.getStoredResources().getByContainerId(surfaceId).stream().anyMatch(storedResource -> storedResource.getAmount() > 0)) {
            throw ExceptionFactory.forbiddenOperation("Cannot terraform surface while abandoned resources are present on it. SurfaceId: " + surfaceId);
        }

        Surface surface = gameData.getSurfaces()
            .findByIdValidated(surfaceId);

        ConstructionRequirements constructionRequirements = terraformingPossibilitiesService.getOptional(surface.getSurfaceType())
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed."))
            .stream()
            .filter(terraformingPossibilities -> terraformingPossibilities.getSurfaceType() == surfaceType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(surface.getSurfaceType() + " cannot be terraformed to " + surfaceType))
            .getConstructionRequirements();

        Construction terraformation = constructionFactory.create(
            surfaceId,
            planetId,
            constructionRequirements.getRequiredWorkPoints(),
            surfaceTypeString
        );

        game.getEventLoop()
            .processWithWait(() -> {
                GameProgressDiff progressDiff = game.getProgressDiff();

                constructionRequirements.getRequiredResources()
                    .forEach((dataId, amount) -> reservedStorageFactory.save(progressDiff, gameData, surfaceId, ContainerType.SURFACE, terraformation.getConstructionId(), dataId, amount));

                gameData.getConstructions()
                    .add(terraformation);

                TerraformationProcess terraformationProcess = terraformationProcessFactory.create(game, planetId, terraformation);

                gameData.getProcesses()
                    .add(terraformationProcess);

                progressDiff.save(terraformationProcess.toModel());
                progressDiff.save(constructionConverter.toModel(gameData.getGameId(), terraformation));
            })
            .getOrThrow();
    }
}
