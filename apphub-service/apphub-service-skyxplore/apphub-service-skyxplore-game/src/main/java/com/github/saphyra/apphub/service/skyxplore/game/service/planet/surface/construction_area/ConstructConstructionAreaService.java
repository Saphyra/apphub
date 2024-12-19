package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreaConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreaFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area.ConstructConstructionAreaProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area.ConstructConstructionAreaProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructConstructionAreaService {
    private final ConstructionAreaDataService constructionAreaDataService;
    private final GameDao gameDao;
    private final ConstructionFactory constructionFactory;
    private final ConstructionAreaFactory constructionAreaFactory;
    private final ResourceAllocationService resourceAllocationService;
    private final ConstructConstructionAreaProcessFactory constructConstructionAreaProcessFactory;
    private final ConstructionConverter constructionConverter;
    private final ConstructionAreaConverter constructionAreaConverter;

    void constructConstructionArea(UUID userId, UUID surfaceId, String constructionAreaDataId) {
        ValidationUtil.containsKey(constructionAreaDataId, constructionAreaDataService, "dataId", "dataId");

        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        Surface surface = gameData.getSurfaces()
            .findBySurfaceIdValidated(surfaceId);
        UUID planetId = surface.getPlanetId();

        //Construction must happen on own planet
        if (!userId.equals(gameData.getPlanets().findByIdValidated(planetId).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot construct constructionArea on planet " + planetId);
        }

        //There must be no terraformation in progress
        if (gameData.getConstructions().findByExternalReference(surfaceId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Terraformation already in progress on surface " + surfaceId);
        }

        //Surface must be empty
        if (gameData.getConstructionAreas().findBySurfaceId(surfaceId).isPresent()) {
            throw ExceptionFactory.forbiddenOperation("There is already a constructionArea on surface " + surfaceId);
        }

        ConstructionAreaData constructionAreaData = constructionAreaDataService.get(constructionAreaDataId);
        //Surface must be supported type
        if (!constructionAreaData.getSupportedSurfaces().contains(surface.getSurfaceType())) {
            throw ExceptionFactory.forbiddenOperation(constructionAreaDataId + " cannot built on surfaceType " + surface.getSurfaceType());
        }

        ConstructionRequirements constructionRequirements = constructionAreaData.getConstructionRequirements();
        ConstructionArea constructionArea = constructionAreaFactory.create(planetId, surfaceId, constructionAreaDataId);
        log.info("{} created.", constructionArea);

        Construction construction = constructionFactory.create(
            constructionArea.getConstructionAreaId(),
            ConstructionType.CONSTRUCTION_AREA,
            planetId,
            constructionRequirements.getRequiredWorkPoints()
        );
        log.info("{} created.", construction);

        game.getEventLoop()
            .processWithWait(() -> {
                GameProgressDiff progressDiff = game.getProgressDiff();

                resourceAllocationService.processResourceRequirements(
                    progressDiff,
                    gameData,
                    planetId,
                    construction.getConstructionId(),
                    constructionRequirements.getRequiredResources()
                );

                ConstructConstructionAreaProcess process = constructConstructionAreaProcessFactory.create(game, construction);
                log.info("{} created.", process);

                gameData.getConstructionAreas()
                    .add(constructionArea);
                gameData.getConstructions()
                    .add(construction);
                gameData.getProcesses()
                    .add(process);

                progressDiff.save(constructionAreaConverter.convert(gameData.getGameId(), constructionArea));
                progressDiff.save(process.toModel());
                progressDiff.save(constructionConverter.toModel(gameData.getGameId(), construction));
            })
            .getOrThrow();
    }
}
