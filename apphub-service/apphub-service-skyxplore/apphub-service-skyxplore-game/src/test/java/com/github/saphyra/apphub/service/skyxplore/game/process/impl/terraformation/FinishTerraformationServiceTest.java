package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SurfaceToModelConverter;
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
public class FinishTerraformationServiceTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private SurfaceToModelConverter surfaceToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @InjectMocks
    private FinishTerraformationService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Construction terraformation;

    @Mock
    private Surface surface;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Mock
    private SurfaceModel surfaceModel;

    @Test
    public void finishTerraformation() {
        given(surface.getTerraformation()).willReturn(terraformation);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(terraformation.getData()).willReturn(SurfaceType.DESERT.name());
        given(game.getGameId()).willReturn(GAME_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(CollectionUtils.toMap(DATA_ID, planetBuildingOverviewResponse));
        given(surfaceToModelConverter.convert(surface, GAME_ID)).willReturn(surfaceModel);

        underTest.finishTerraformation(syncCache, game, planet, surface);

        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, planet, CONSTRUCTION_ID);
        verify(surface).setSurfaceType(SurfaceType.DESERT);
        verify(surface).setTerraformation(null);
        verify(syncCache).deleteGameItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        verify(syncCache).saveGameItem(surfaceModel);

        ArgumentCaptor<Runnable> queueItemDeletedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(CONSTRUCTION_ID), queueItemDeletedArgumentCaptor.capture());
        queueItemDeletedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, CONSTRUCTION_ID);

        ArgumentCaptor<Runnable> surfaceModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED), eq(SURFACE_ID), surfaceModifiedArgumentCaptor.capture());
        surfaceModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);

        ArgumentCaptor<Runnable> buildingDetailsModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED), eq(PLANET_ID), buildingDetailsModifiedArgumentCaptor.capture());
        buildingDetailsModifiedArgumentCaptor.getValue()
            .run();
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.toMap(DATA_ID, planetBuildingOverviewResponse));
    }
}