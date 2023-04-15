package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.AllBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpgradeBuildingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 234;
    private static final Integer REQUIRED_WORK_POINTS = 324;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int PARALLEL_WORKERS = 245;
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private AllBuildingService allBuildingService;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private ResourceAllocationService resourceAllocationService;

    @Mock
    private ConstructionProcessFactory constructionProcessFactory;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @InjectMocks
    private UpgradeBuildingService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private BuildingData buildingData;

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
    private Constructions constructions;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private Buildings buildings;

    @Mock
    private Surfaces surfaces;

    @Mock
    private SyncCache syncCache;

    @Test
    void deconstructionAlreadyInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(deconstruction));

        Throwable ex = catchThrowable(() -> underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void constructionAlreadyInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(construction));

        Throwable ex = catchThrowable(() -> underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void buildingAtMaxLevel() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getDataId()).willReturn(DATA_ID);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());

        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);

        given(allBuildingService.get(DATA_ID)).willReturn(buildingData);
        given(buildingData.getConstructionRequirements()).willReturn(new HashMap<>());

        Throwable ex = catchThrowable(() -> underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void upgradeBuilding() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getDataId()).willReturn(DATA_ID);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));

        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);

        given(allBuildingService.get(DATA_ID)).willReturn(buildingData);
        given(buildingData.getConstructionRequirements()).willReturn(CollectionUtils.singleValueMap(LEVEL + 1, constructionRequirements));
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(constructionRequirements.getParallelWorkers()).willReturn(PARALLEL_WORKERS);
        given(constructionFactory.create(BUILDING_ID, ConstructionType.CONSTRUCTION, PLANET_ID, PARALLEL_WORKERS, REQUIRED_WORK_POINTS)).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);

        given(gameData.getProcesses()).willReturn(processes);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(Runnable.class), eq(syncCache))).willReturn(executionResult);

        given(constructionProcessFactory.create(gameData, PLANET_ID, CONSTRUCTION_ID)).willReturn(constructionProcess);
        given(planet.getOwner()).willReturn(USER_ID);
        given(syncCacheFactory.create(game)).willReturn(syncCache);

        underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(resourceAllocationService).processResourceRequirements(syncCache, gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID, Collections.emptyMap());
        verify(constructions).add(construction);
        verify(syncCache).constructionCreated(USER_ID, PLANET_ID, construction, surface, constructionProcess);
        verify(processes).add(constructionProcess);
        verify(executionResult).getOrThrow();
    }
}