package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CancelConstructionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private GameDao gameDao;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private SyncCacheFactory syncCacheFactory;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private BuildingToModelConverter buildingToModelConverter;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @InjectMocks
    private CancelConstructionService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private SurfaceMap surfaceMap;

    @Mock
    private Surface surface;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private SyncCache syncCache;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private ExecutionResult<SurfaceResponse> executionResult;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Captor
    private ArgumentCaptor<Callable<SurfaceResponse>> argumentCaptor;

    @Before
    public void setUp() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(surfaceMap);
        given(surfaceMap.findByBuildingIdValidated(BUILDING_ID)).willReturn(surface);
        given(surface.getBuilding()).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(syncCacheFactory.create()).willReturn(syncCache);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(game.getGameId()).willReturn(GAME_ID);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
    }

    @Test
    public void cancelConstructionOfConstruction() throws Exception {
        given(surface.getBuilding()).willReturn(building);
        given(building.getConstruction()).willReturn(construction);
        given(building.getLevel()).willReturn(0);
        given(surfaceMap.values()).willReturn(List.of(surface));
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));

        given(game.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.CONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithResponseAndWait(any(Callable.class), eq(syncCache))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);
        given(buildingToModelConverter.convert(building, GAME_ID)).willReturn(buildingModel);

        underTest.cancelConstructionOfConstruction(USER_ID, PLANET_ID, CONSTRUCTION_ID);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .call();

        verify(syncCache).deleteGameItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        verify(building).setConstruction(null);
        verify(surface).setBuilding(null);
        verify(process).cancel(syncCache);
        verify(syncCache).deleteGameItem(BUILDING_ID, GameItemType.BUILDING);
        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, planet, CONSTRUCTION_ID);
        verify(syncCache).saveGameItem(buildingModel);

        ArgumentCaptor<Runnable> runnableArgumentCaptor1 = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(PLANET_ID), runnableArgumentCaptor1.capture());
        runnableArgumentCaptor1.getValue()
            .run();

        ArgumentCaptor<Runnable> runnableArgumentCaptor2 = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), runnableArgumentCaptor2.capture());
        runnableArgumentCaptor2.getValue()
            .run();

        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, CONSTRUCTION_ID);
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));
    }

    @Test
    public void cancelConstructionOfBuilding_constructionNotFound() {
        given(building.getConstruction()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.cancelConstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void cancelConstructionOfUpgrade() throws Exception {
        given(building.getConstruction()).willReturn(construction);
        given(building.getLevel()).willReturn(0);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));

        given(game.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.CONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithResponseAndWait(any(Callable.class), eq(syncCache))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);
        given(buildingToModelConverter.convert(building, GAME_ID)).willReturn(buildingModel);

        SurfaceResponse result = underTest.cancelConstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        assertThat(result).isEqualTo(surfaceResponse);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .call();

        verify(syncCache).deleteGameItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        verify(building).setConstruction(null);
        verify(surface).setBuilding(null);
        verify(process).cancel(syncCache);
        verify(syncCache).deleteGameItem(BUILDING_ID, GameItemType.BUILDING);
        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, planet, CONSTRUCTION_ID);
        verify(syncCache).saveGameItem(buildingModel);

        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(PLANET_ID), runnableArgumentCaptor.capture());
        runnableArgumentCaptor.getValue()
            .run();

        ArgumentCaptor<Runnable> runnableArgumentCaptor2 = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), runnableArgumentCaptor2.capture());
        runnableArgumentCaptor2.getValue()
            .run();

        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, CONSTRUCTION_ID);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));
    }
}