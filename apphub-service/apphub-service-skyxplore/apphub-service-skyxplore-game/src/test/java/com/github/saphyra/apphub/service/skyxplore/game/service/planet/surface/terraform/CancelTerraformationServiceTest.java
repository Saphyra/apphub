package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CancelTerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @InjectMocks
    private CancelTerraformationService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction terraformation;

    @Mock
    private SyncCache syncCache;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private Map<String, PlanetBuildingOverviewResponse> buildingOverviewResponse;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private ExecutionResult<SurfaceResponse> executionResult;

    @Mock
    private Constructions constructions;

    @Mock
    private Surfaces surfaces;

    @Captor
    private ArgumentCaptor<Callable<SurfaceResponse>> callableArgumentCaptor;

    @Test
    public void cancelTerraformationQueueItem() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(terraformation.getExternalReference()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));

        //Common
        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(gameData.getProcesses()).willReturn(processes);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.TERRAFORMATION)).willReturn(process);

        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(surfaceToResponseConverter.convert(gameData, surface)).willReturn(surfaceResponse);
        //noinspection unchecked
        given(eventLoop.processWithResponseAndWait(any(Callable.class), eq(syncCache))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(gameData, PLANET_ID)).willReturn(buildingOverviewResponse);

        underTest.cancelTerraformationQueueItem(USER_ID, PLANET_ID, CONSTRUCTION_ID);

        //Common
        verify(eventLoop).processWithResponseAndWait(callableArgumentCaptor.capture(), eq(syncCache));
        SurfaceResponse surfaceResponseResult = callableArgumentCaptor.getValue()
            .call();

        assertThat(surfaceResponseResult).isEqualTo(surfaceResponseResult);

        verify(process).cancel(syncCache);
        verify(constructions).deleteById(CONSTRUCTION_ID);
        verify(gameDataProxy).deleteItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID);

        ArgumentCaptor<Runnable> queueItemDeletedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(PLANET_ID), queueItemDeletedArgumentCaptor.capture());
        queueItemDeletedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, CONSTRUCTION_ID);

        ArgumentCaptor<Runnable> buildingDetailsModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), buildingDetailsModifiedArgumentCaptor.capture());
        buildingDetailsModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, buildingOverviewResponse);
        //End common

        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);
    }

    @Test
    public void cancelTerraformationOfSurface() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReferenceValidated(SURFACE_ID)).willReturn(terraformation);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));

        //Common
        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(gameData.getProcesses()).willReturn(processes);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.TERRAFORMATION)).willReturn(process);

        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(surfaceToResponseConverter.convert(gameData, surface)).willReturn(surfaceResponse);
        //noinspection unchecked
        given(eventLoop.processWithResponseAndWait(any(Callable.class), eq(syncCache))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(gameData, PLANET_ID)).willReturn(buildingOverviewResponse);

        SurfaceResponse result = underTest.cancelTerraformationOfSurface(USER_ID, PLANET_ID, SURFACE_ID);

        verify(eventLoop).processWithResponseAndWait(callableArgumentCaptor.capture(), eq(syncCache));
        SurfaceResponse surfaceResponseResult = callableArgumentCaptor.getValue()
            .call();

        assertThat(surfaceResponseResult).isEqualTo(surfaceResponseResult);

        verify(process).cancel(syncCache);
        verify(constructions).deleteById(CONSTRUCTION_ID);
        verify(gameDataProxy).deleteItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID);

        ArgumentCaptor<Runnable> queueItemDeletedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(PLANET_ID), queueItemDeletedArgumentCaptor.capture());
        queueItemDeletedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, CONSTRUCTION_ID);

        ArgumentCaptor<Runnable> buildingDetailsModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), buildingDetailsModifiedArgumentCaptor.capture());
        buildingDetailsModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, buildingOverviewResponse);

        assertThat(result).isEqualTo(surfaceResponse);
    }
}