package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction.BuildingDeconstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.DeconstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructionUpdateServiceTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final int COMPLETED_WORK_POINTS = 4236;
    private static final Integer CURRENT_WORK_POINTS = 2456;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private DeconstructionToModelConverter deconstructionToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @InjectMocks
    private DeconstructionUpdateService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Buildings buildings;

    @Mock
    private Surfaces surfaces;

    @Test
    void updateDeconstruction() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionId(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(gameData.getBuildings()).willReturn(buildings);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_ID);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);

        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(deconstruction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(deconstructionToModelConverter.convert(GAME_ID, deconstruction)).willReturn(deconstructionModel);
        given(planet.getOwner()).willReturn(USER_ID);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaceToResponseConverter.convert(gameData, surface)).willReturn(surfaceResponse);
        given(buildingDeconstructionToQueueItemConverter.convert(gameData, deconstruction)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, gameData, PLANET_ID)).willReturn(queueResponse);

        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(PLANET_ID, planet, new Planets()));
        given(planet.getOwner()).willReturn(USER_ID);

        underTest.updateDeconstruction(syncCache, gameData, PLANET_ID, DECONSTRUCTION_ID, COMPLETED_WORK_POINTS);

        verify(deconstruction).setCurrentWorkPoints(CURRENT_WORK_POINTS + COMPLETED_WORK_POINTS);
        verify(syncCache).saveGameItem(deconstructionModel);

        ArgumentCaptor<Runnable> planetSurfaceModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED), eq(SURFACE_ID), planetSurfaceModifiedArgumentCaptor.capture());
        planetSurfaceModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);

        ArgumentCaptor<Runnable> queueItemModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED), eq(DECONSTRUCTION_ID), queueItemModifiedArgumentCaptor.capture());
        queueItemModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);
    }
}