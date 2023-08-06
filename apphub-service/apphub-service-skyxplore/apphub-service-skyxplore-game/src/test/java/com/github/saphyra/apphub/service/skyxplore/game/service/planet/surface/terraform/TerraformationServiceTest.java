package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation.TerraformationProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation.TerraformationProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 436;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int PARALLEL_WORKERS = 254;

    @Mock
    private TerraformingPossibilitiesService terraformingPossibilitiesService;

    @Mock
    private GameDao gameDao;

    @Mock
    private ResourceAllocationService resourceAllocationService;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private TerraformationProcessFactory terraformationProcessFactory;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @InjectMocks
    private TerraformationService underTest;

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
    private Construction terraformation;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private TerraformationProcess terraformationProcess;

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
    private SyncCache syncCache;

    @Test
    public void invalidSurfaceType() {
        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, "afe"));

        ExceptionValidator.validateInvalidParam(ex, "surfaceType", "invalid value");
    }

    @Test
    public void terraformationAlreadyInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(terraformation));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void surfaceNotEmpty() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(building));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceTypeCannotBeTerraformed() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.empty());
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceCannotBeTerraformedToGivenType() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities()));
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void terraform() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities(List.of(terraformingPossibility))));
        given(terraformingPossibility.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(terraformingPossibility.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionFactory.create(SURFACE_ID, ConstructionType.TERRAFORMATION, PLANET_ID, PARALLEL_WORKERS, REQUIRED_WORK_POINTS, SurfaceType.CONCRETE.name())).willReturn(terraformation);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(constructionRequirements.getParallelWorkers()).willReturn(PARALLEL_WORKERS);
        given(terraformationProcessFactory.create(gameData, PLANET_ID, terraformation)).willReturn(terraformationProcess);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(Runnable.class), eq(syncCache))).willReturn(executionResult);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(planet.getOwner()).willReturn(USER_ID);
        given(syncCacheFactory.create(game)).willReturn(syncCache);

        underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name());

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(resourceAllocationService).processResourceRequirements(syncCache, gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID, Collections.emptyMap());
        verify(syncCache).terraformationCreated(USER_ID, PLANET_ID, terraformation, surface, terraformationProcess);
        verify(processes).add(terraformationProcess);
        verify(executionResult).getOrThrow();
    }
}