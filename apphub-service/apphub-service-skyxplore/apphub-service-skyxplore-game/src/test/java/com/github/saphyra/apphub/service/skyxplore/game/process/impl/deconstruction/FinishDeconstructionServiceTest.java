package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FinishDeconstructionServiceTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @InjectMocks
    private FinishDeconstructionService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Planet planet;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Test
    void finishDeconstruction() {
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_ID);
        given(planet.findSurfaceByBuildingIdValidated(BUILDING_ID)).willReturn(surface);
        given(surface.getBuilding()).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(Map.of(DATA_ID, planetBuildingOverviewResponse));

        underTest.finishDeconstruction(syncCache, planet, deconstruction);

        verify(surface).setBuilding(null);
        verify(gameDataProxy).deleteItem(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
        verify(gameDataProxy).deleteItem(BUILDING_ID, GameItemType.BUILDING);

        ArgumentCaptor<Runnable> queueItemDeletedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(DECONSTRUCTION_ID), queueItemDeletedArgumentCaptor.capture());
        queueItemDeletedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, DECONSTRUCTION_ID);

        ArgumentCaptor<Runnable> planetSurfaceModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED), eq(SURFACE_ID), planetSurfaceModifiedArgumentCaptor.capture());
        planetSurfaceModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);

        ArgumentCaptor<Runnable> buildingDetailsModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), buildingDetailsModifiedArgumentCaptor.capture());
        buildingDetailsModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, Map.of(DATA_ID, planetBuildingOverviewResponse));
    }
}