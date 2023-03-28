package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
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
    private GameData gameData;

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

    @Mock
    private Constructions constructions;

    @Mock
    private Surfaces surfaces;

    @Test
    public void finishTerraformation() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(PLANET_ID, planet, new Planets()));
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);

        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(terraformation.getData()).willReturn(SurfaceType.DESERT.name());
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(terraformation.getExternalReference()).willReturn(SURFACE_ID);
        given(surfaceToResponseConverter.convert(gameData, surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(gameData, PLANET_ID)).willReturn(CollectionUtils.toMap(DATA_ID, planetBuildingOverviewResponse));
        given(surfaceToModelConverter.convert(GAME_ID, surface)).willReturn(surfaceModel);

        underTest.finishTerraformation(syncCache, gameData, PLANET_ID, terraformation);

        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID);
        verify(surface).setSurfaceType(SurfaceType.DESERT);
        verify(constructions).deleteById(CONSTRUCTION_ID);
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