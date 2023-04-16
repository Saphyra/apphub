package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelDeconstructionServiceTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private CancelDeconstructionService underTest;

    @Mock
    private Planet planet;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private SyncCache syncCache;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Buildings buildings;

    @Test
    void cancelDeconstructionOfDeconstruction() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionId(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_ID);
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);

        given(syncCacheFactory.create(game)).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithWait(any(Runnable.class), eq(syncCache))).willReturn(executionResult);

        underTest.cancelDeconstructionOfDeconstruction(USER_ID, PLANET_ID, DECONSTRUCTION_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(deconstructions).remove(deconstruction);
        verify(process).cancel(syncCache);
        verify(executionResult).getOrThrow();

        verify(syncCache).deconstructionCancelled(USER_ID, PLANET_ID, DECONSTRUCTION_ID, surface);
    }

    @Test
    void cancelDeconstructionOfBuilding() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReferenceValidated(BUILDING_ID)).willReturn(deconstruction);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);


        given(syncCacheFactory.create(game)).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithWait(any(Runnable.class), eq(syncCache))).willReturn(executionResult);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(planet.getOwner()).willReturn(USER_ID);

        underTest.cancelDeconstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(deconstructions).remove(deconstruction);
        verify(process).cancel(syncCache);
        verify(executionResult).getOrThrow();

        verify(syncCache).deconstructionCancelled(USER_ID, PLANET_ID, DECONSTRUCTION_ID, surface);
    }
}