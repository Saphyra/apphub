package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module.ConstructBuildingModuleProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module.ConstructBuildingModuleProcessFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConstructBuildingModuleServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String BUILDING_MODULE_DATA_ID = "building-module-data-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String CONSTRUCTION_AREA_DATA_ID = "construction-area-data-id";
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 34;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer RESOURCE_AMOUNT = 234;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private  GameDao gameDao;

    @Mock
    private  BuildingModuleDataService buildingModuleDataService;

    @Mock
    private  ConstructionAreaDataService constructionAreaDataService;

    @Mock
    private  BuildingModuleFactory buildingModuleFactory;

    @Mock
    private  ConstructionFactory constructionFactory;

    @Mock
    private  BuildingModuleConverter buildingModuleConverter;

    @Mock
    private  ConstructionConverter constructionConverter;

    @Mock
    private  ConstructBuildingModuleProcessFactory constructBuildingModuleProcessFactory;

    @Mock
    private  ReservedStorageFactory reservedStorageFactory;

    @InjectMocks
    private ConstructBuildingModuleService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModuleData buildingModuleData;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private ConstructionAreaData constructionAreaData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ConstructBuildingModuleProcess process;

    @Mock
    private Processes processes;

    @Mock
    private BuildingModuleModel buildingModuleModel;

    @Mock
    private ProcessModel processModel;

    @Mock
    private ConstructionModel constructionModel;

    @Test
    void nullBuildingModuleDataId() {
        ExceptionValidator.validateInvalidParam(() -> underTest.constructBuildingModule(USER_ID, CONSTRUCTION_AREA_ID, null), "buildingModuleDataId", "must not be null");
    }

    @Test
    void buildingModuleDataNotFound() {
        ExceptionValidator.validateInvalidParam(() -> underTest.constructBuildingModule(USER_ID, CONSTRUCTION_AREA_ID, BUILDING_MODULE_DATA_ID), "buildingModuleData", "must not be null");
    }

    @Test
    void forbiddenOperation() {
        given(buildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(buildingModuleData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(null);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.constructBuildingModule(USER_ID, CONSTRUCTION_AREA_ID, BUILDING_MODULE_DATA_ID));
    }

    @Test
    void slotNotSupported() {
        given(buildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(buildingModuleData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(constructionArea.getDataId()).willReturn(CONSTRUCTION_AREA_DATA_ID);
        given(constructionAreaDataService.get(CONSTRUCTION_AREA_DATA_ID)).willReturn(constructionAreaData);
        given(constructionAreaData.getSlots()).willReturn(Map.of());
        given(buildingModuleData.getCategory()).willReturn(BuildingModuleCategory.CULTURAL);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.constructBuildingModule(USER_ID, CONSTRUCTION_AREA_ID, BUILDING_MODULE_DATA_ID));
    }

    @Test
    void notEnoughEmptySlots() {
        given(buildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(buildingModuleData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(constructionArea.getDataId()).willReturn(CONSTRUCTION_AREA_DATA_ID);
        given(constructionAreaDataService.get(CONSTRUCTION_AREA_DATA_ID)).willReturn(constructionAreaData);
        given(constructionAreaData.getSlots()).willReturn(Map.of(BuildingModuleCategory.CULTURAL, 1));
        given(buildingModuleData.getCategory()).willReturn(BuildingModuleCategory.CULTURAL);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.constructBuildingModule(USER_ID, CONSTRUCTION_AREA_ID, BUILDING_MODULE_DATA_ID));
    }

    @Test
    void constructBuildingModule() {
        given(buildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(buildingModuleData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(constructionArea.getDataId()).willReturn(CONSTRUCTION_AREA_DATA_ID);
        given(constructionAreaDataService.get(CONSTRUCTION_AREA_DATA_ID)).willReturn(constructionAreaData);
        given(constructionAreaData.getSlots()).willReturn(Map.of(BuildingModuleCategory.CULTURAL, 2));
        given(buildingModuleData.getCategory()).willReturn(BuildingModuleCategory.CULTURAL);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(buildingModuleFactory.create(PLANET_ID, CONSTRUCTION_AREA_ID, BUILDING_MODULE_DATA_ID)).willReturn(buildingModule);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(buildingModuleData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionFactory.create(BUILDING_MODULE_ID, ConstructionType.BUILDING_MODULE, PLANET_ID, REQUIRED_WORK_POINTS)).willReturn(construction);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();

            return ExecutionResult.success(null);
        });
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(RESOURCE_DATA_ID, RESOURCE_AMOUNT));
        given(constructBuildingModuleProcessFactory.create(game, construction)).willReturn(process);
        given(gameData.getProcesses()).willReturn(processes);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(buildingModuleConverter.convert(GAME_ID, buildingModule)).willReturn(buildingModuleModel);
        given(process.toModel()).willReturn(processModel);
        given(constructionConverter.toModel(GAME_ID, construction)).willReturn(constructionModel);
        given(gameData.getConstructions()).willReturn(constructions);

        underTest.constructBuildingModule(USER_ID, CONSTRUCTION_AREA_ID, BUILDING_MODULE_DATA_ID);

        then(buildingModules).should().add(buildingModule);
        then(constructions).should().add(construction);
        then(processes).should().add(process);
        then(progressDiff).should().save(buildingModuleModel);
        then(progressDiff).should().save(processModel);
        then(progressDiff).should().save(constructionModel);
    }
}