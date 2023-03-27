package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
    private static final String DATA_ID = "data-id";

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private CancelDeconstructionService underTest;

    @Mock
    private Planet planet;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private SurfaceMap surfaceMap;

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
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private ExecutionResult<SurfaceResponse> executionResult;

    @Captor
    private ArgumentCaptor<Callable<SurfaceResponse>> argumentCaptor;

    @Test
    void deconstructionNotFound() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(surfaceMap);
        given(surfaceMap.findByBuildingIdValidated(BUILDING_ID)).willReturn(surface);
        given(surface.getBuilding()).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(building.getDeconstruction()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.cancelDeconstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void cancelDeconstructionOfDeconstruction() throws Exception {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(surfaceMap);
        given(surface.getBuilding()).willReturn(building);
        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(surface.getBuilding()).willReturn(building);
        given(building.getDeconstruction()).willReturn(deconstruction);
        given(surfaceMap.values()).willReturn(List.of(surface));
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(Map.of(DATA_ID, planetBuildingOverviewResponse));

        given(game.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithResponseAndWait(any(Callable.class), eq(syncCache))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);

        underTest.cancelDeconstructionOfDeconstruction(USER_ID, PLANET_ID, DECONSTRUCTION_ID);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .call();

        verify(syncCache).deleteGameItem(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
        verify(building).setDeconstruction(null);
        verify(process).cancel(syncCache);

        ArgumentCaptor<Runnable> runnableArgumentCaptor1 = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(PLANET_ID), runnableArgumentCaptor1.capture());
        runnableArgumentCaptor1.getValue()
            .run();

        ArgumentCaptor<Runnable> runnableArgumentCaptor2 = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), runnableArgumentCaptor2.capture());
        runnableArgumentCaptor2.getValue()
            .run();

        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, DECONSTRUCTION_ID);
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.toMap(DATA_ID, planetBuildingOverviewResponse));
    }

    @Test
    void cancelDeconstructionOfBuilding() throws Exception {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(surfaceMap);
        given(surfaceMap.findByBuildingIdValidated(BUILDING_ID)).willReturn(surface);
        given(surface.getBuilding()).willReturn(building);
        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(building.getDeconstruction()).willReturn(deconstruction);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(Map.of(DATA_ID, planetBuildingOverviewResponse));

        given(game.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithResponseAndWait(any(Callable.class), eq(syncCache))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.cancelDeconstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        assertThat(result).isEqualTo(surfaceResponse);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .call();

        verify(syncCache).deleteGameItem(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
        verify(building).setDeconstruction(null);
        verify(process).cancel(syncCache);

        ArgumentCaptor<Runnable> runnableArgumentCaptor1 = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(PLANET_ID), runnableArgumentCaptor1.capture());
        runnableArgumentCaptor1.getValue()
            .run();

        ArgumentCaptor<Runnable> runnableArgumentCaptor2 = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), runnableArgumentCaptor2.capture());
        runnableArgumentCaptor2.getValue()
            .run();

        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, DECONSTRUCTION_ID);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.toMap(DATA_ID, planetBuildingOverviewResponse));
    }
}