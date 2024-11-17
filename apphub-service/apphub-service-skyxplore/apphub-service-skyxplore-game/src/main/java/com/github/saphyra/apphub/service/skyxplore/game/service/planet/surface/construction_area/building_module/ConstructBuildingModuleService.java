package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModuleConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModuleFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module.ConstructBuildingModuleProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module.ConstructBuildingModuleProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ConstructBuildingModuleService {
    private final GameDao gameDao;
    private final BuildingModuleDataService buildingModuleDataService;
    private final ConstructionAreaDataService constructionAreaDataService;
    private final BuildingModuleFactory buildingModuleFactory;
    private final ConstructionFactory constructionFactory;
    private final ResourceAllocationService resourceAllocationService;
    private final BuildingModuleConverter buildingModuleConverter;
    private final ConstructionConverter constructionConverter;
    private final ConstructBuildingModuleProcessFactory constructBuildingModuleProcessFactory;

    void constructBuildingModule(UUID userId, UUID constructionAreaId, String buildingModuleDataId) {
        ValidationUtil.notNull(buildingModuleDataId, "buildingModuleDataId");

        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        BuildingModuleData buildingModuleData = buildingModuleDataService.getValidated(buildingModuleDataId, "buildingModule");
        ConstructionRequirements constructionRequirements = buildingModuleData.getConstructionRequirements();
        ConstructionArea constructionArea = gameData.getConstructionAreas()
            .findByConstructionAreaIdValidated(constructionAreaId);
        ConstructionAreaData constructionAreaData = constructionAreaDataService.get(constructionArea.getDataId());
        Surface surface = gameData.getSurfaces()
            .findBySurfaceIdValidated(constructionArea.getSurfaceId());
        UUID planetId = surface.getPlanetId();

        //Construction must happen on own planet
        if (!gameData.getPlanets().findByIdValidated(planetId).getOwner().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot construct constructionArea on planet " + planetId);
        }

        //ConstructionArea must available place on supported slot
        if (constructionAreaData.getSlots().getOrDefault(buildingModuleData.getCategory(), 0) <= getBuildingModuleCount(gameData, constructionAreaId, buildingModuleData.getCategory())) {
            throw ExceptionFactory.forbiddenOperation("%s has no more empty %s slots.".formatted(constructionAreaId, buildingModuleData.getCategory()));
        }

        BuildingModule buildingModule = buildingModuleFactory.create(planetId, constructionAreaId, buildingModuleDataId);
        log.info("{} created.", buildingModule);

        Construction construction = constructionFactory.create(
            buildingModule.getBuildingModuleId(),
            ConstructionType.BUILDING_MODULE,
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

                ConstructBuildingModuleProcess process = constructBuildingModuleProcessFactory.create(game, construction);
                log.info("{} created.", process);

                gameData.getBuildingModules()
                    .add(buildingModule);
                gameData.getConstructions()
                    .add(construction);
                gameData.getProcesses()
                    .add(process);

                progressDiff.save(buildingModuleConverter.convert(gameData.getGameId(), buildingModule));
                progressDiff.save(process.toModel());
                progressDiff.save(constructionConverter.toModel(gameData.getGameId(), construction));
            })
            .getOrThrow();
    }

    private long getBuildingModuleCount(GameData gameData, UUID constructionAreaId, BuildingModuleCategory category) {
        return gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .stream()
            .filter(buildingModule -> buildingModuleDataService.get(buildingModule.getDataId()).getCategory() == category)
            .count();
    }
}
