package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructBuildingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private DeconstructionFactory deconstructionFactory;

    @Mock
    private DeconstructionProcessFactory deconstructionProcessFactory;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private DeconstructionPreconditions deconstructionPreconditions;

    @InjectMocks
    private DeconstructBuildingService underTest;

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
    private Deconstruction deconstruction;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private DeconstructionProcess deconstructionProcess;

    @Mock
    private Processes processes;

    @Mock
    private Constructions constructions;

    @Mock
    private Buildings buildings;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private SyncCache syncCache;

    @Test
    void constructionInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(construction));

        Throwable ex = catchThrowable(() -> underTest.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    void deconstructBuilding() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(deconstructionFactory.create(BUILDING_ID, PLANET_ID)).willReturn(deconstruction);
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(Runnable.class), eq(syncCache))).willReturn(executionResult);
        given(deconstructionProcessFactory.create(gameData, PLANET_ID, DECONSTRUCTION_ID)).willReturn(deconstructionProcess);
        given(gameData.getProcesses()).willReturn(processes);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(syncCacheFactory.create(game)).willReturn(syncCache);

        underTest.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        verify(deconstructionPreconditions).checkIfBuildingCanBeDeconstructed(gameData, building);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(executionResult).getOrThrow();
        verify(deconstructions).add(deconstruction);
        verify(syncCache).deconstructionCreated(USER_ID, PLANET_ID, deconstruction, surface, deconstructionProcess);
        verify(processes).add(deconstructionProcess);
    }
}