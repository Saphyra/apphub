package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.AllBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcessFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConstructNewBuildingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 2314;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private AllBuildingService allBuildingService;

    @Mock
    private BuildingFactory buildingFactory;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private ResourceAllocationService resourceAllocationService;

    @Mock
    private ConstructionProcessFactory constructionProcessFactory;

    @Mock
    private ConstructionConverter constructionConverter;

    @Mock
    private BuildingConverter buildingConverter;

    @InjectMocks
    private ConstructNewBuildingService underTest;

    @Mock
    private BuildingData buildingData;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ConstructionProcess constructionProcess;

    @Mock
    private Processes processes;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Constructions constructions;

    @Mock
    private Buildings buildings;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private ProcessModel processModel;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @BeforeEach
    void setUp(){
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(allBuildingService.getOptional(DATA_ID)).willReturn(Optional.of(buildingData));
    }

    @Test
    public void invalidDataId() {
        given(allBuildingService.getOptional(DATA_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "invalid value");
    }

    @Test
    void forbiddenOperation(){
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));
    }

    @Test
    public void terraformationInProgress() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(construction));

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void surfaceAlreadyOccupied() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(building));

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void buildingCannotBeBuiltToGivenSurfaceType() {
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceIdValidated(SURFACE_ID)).willReturn(surface);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(buildingData.getPlaceableSurfaceTypes()).willReturn(Collections.emptyList());

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void constructNewBuilding() {
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceIdValidated(SURFACE_ID)).willReturn(surface);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(buildingData.getPlaceableSurfaceTypes()).willReturn(List.of(SurfaceType.CONCRETE));
        given(buildingData.getConstructionRequirements()).willReturn(CollectionUtils.singleValueMap(1, constructionRequirements));
        given(buildingFactory.create(DATA_ID, PLANET_ID, SURFACE_ID, 0)).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionFactory.create(BUILDING_ID, ConstructionType.CONSTRUCTION, PLANET_ID, REQUIRED_WORK_POINTS)).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);

        given(constructionProcessFactory.create(game, PLANET_ID, CONSTRUCTION_ID)).willReturn(constructionProcess);
        given(gameData.getProcesses()).willReturn(processes);

        given(game.getGameId()).willReturn(GAME_ID);
        given(constructionConverter.toModel(GAME_ID, construction)).willReturn(constructionModel);
        given(constructionProcess.toModel()).willReturn(processModel);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(buildingConverter.toModel(GAME_ID, building)).willReturn(buildingModel);

        underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        then(eventLoop).should().processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        then(constructions).should().add(construction);
        then(buildings).should().add(building);
        then(resourceAllocationService).should().processResourceRequirements(progressDiff, gameData, PLANET_ID, CONSTRUCTION_ID, Collections.emptyMap());
        then(processes).should().add(constructionProcess);
        then(progressDiff).should().save(constructionModel);
        then(progressDiff).should().save(processModel);
        then(progressDiff).should().save(buildingModel);
        then(executionResult).should().getOrThrow();
    }
}