package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FinishConstructionServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private TickCache tickCache;

    @Mock
    private BuildingToModelConverter buildingToModelConverter;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private WsMessageSender messageSender;

    @InjectMocks
    private FinishConstructionService underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private MessageCache messageCache;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Test
    public void finishConstruction() {
        given(surface.getBuilding()).willReturn(building);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(building.getConstruction()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);
        given(tickCacheItem.getMessageCache()).willReturn(messageCache);

        given(buildingToModelConverter.convert(building, GAME_ID)).willReturn(buildingModel);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);

        underTest.finishConstruction(GAME_ID, planet, surface);

        verify(building).setConstruction(null);
        verify(gameItemCache).save(buildingModel);
        verify(gameItemCache).delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);

        ArgumentCaptor<Runnable> queueItemDeletedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED), eq(CONSTRUCTION_ID), queueItemDeletedArgumentCaptor.capture());
        queueItemDeletedArgumentCaptor.getValue().run();
        verify(messageSender).planetQueueItemDeleted(USER_ID, PLANET_ID, CONSTRUCTION_ID);

        ArgumentCaptor<Runnable> surfaceModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED), eq(SURFACE_ID), surfaceModifiedArgumentCaptor.capture());
        surfaceModifiedArgumentCaptor.getValue().run();
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);
    }
}