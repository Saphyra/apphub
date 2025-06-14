package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module.ConstructBuildingModuleProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module.ConstructBuildingModuleProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructBuildingModuleService {
    private final GameDao gameDao;
    private final BuildingModuleDataService buildingModuleDataService;
    private final ConstructionAreaDataService constructionAreaDataService;
    private final BuildingModuleFactory buildingModuleFactory;
    private final ConstructionFactory constructionFactory;
    private final BuildingModuleConverter buildingModuleConverter;
    private final ConstructionConverter constructionConverter;
    private final ConstructBuildingModuleProcessFactory constructBuildingModuleProcessFactory;
    private final ReservedStorageFactory reservedStorageFactory;

    void constructBuildingModule(UUID userId, UUID constructionAreaId, String buildingModuleDataId) {
        ValidationUtil.notNull(buildingModuleDataId, "buildingModuleDataId");
        BuildingModuleData buildingModuleData = buildingModuleDataService.get(buildingModuleDataId);
        ValidationUtil.notNull(buildingModuleData, "buildingModuleData");

        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();

        ConstructionArea constructionArea = gameData.getConstructionAreas()
            .findByIdValidated(constructionAreaId);
        UUID planetId = constructionArea.getLocation();

        //Construction must happen on own planet
        if (!userId.equals(gameData.getPlanets().findByIdValidated(planetId).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot construct constructionArea on planet " + planetId);
        }

        ConstructionAreaData constructionAreaData = constructionAreaDataService.get(constructionArea.getDataId());
        //ConstructionArea must available place on supported slot
        if (constructionAreaData.getSlots().getOrDefault(buildingModuleData.getCategory(), 0) <= getBuildingModuleCount(gameData, constructionAreaId, buildingModuleData.getCategory())) {
            throw ExceptionFactory.forbiddenOperation("%s has no more empty %s slots.".formatted(constructionAreaId, buildingModuleData.getCategory()));
        }

        ConstructionRequirements constructionRequirements = buildingModuleData.getConstructionRequirements();

        BuildingModule buildingModule = buildingModuleFactory.create(planetId, constructionAreaId, buildingModuleDataId);
        log.info("{} created.", buildingModule);

        Construction construction = constructionFactory.create(
            buildingModule.getBuildingModuleId(),
            planetId,
            constructionRequirements.getRequiredWorkPoints()
        );
        log.info("{} created.", construction);

        game.getEventLoop()
            .processWithWait(() -> {
                GameProgressDiff progressDiff = game.getProgressDiff();

                constructionRequirements.getRequiredResources()
                    .forEach((dataId, amount) -> reservedStorageFactory.save(progressDiff, gameData, constructionArea.getSurfaceId(), ContainerType.SURFACE, construction.getConstructionId(), dataId, amount));

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
