package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
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
public class ConstructionUpdateServiceTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int COMPLETED_WORK_POINTS = 4536;
    private static final int CURRENT_WORK_POINTS = 14;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @Mock
    private BuildingConstructionToQueueItemConverter buildingConstructionToQueueItemConverter;

    @InjectMocks
    private ConstructionUpdateService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Test
    public void updateConstruction() {
        given(planet.getSurfaces()).willReturn(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface, new SurfaceMap()));
        given(surface.getBuilding()).willReturn(building);
        given(building.getConstruction()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);

        given(construction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(game.getGameId()).willReturn(GAME_ID);
        given(constructionToModelConverter.convert(construction, GAME_ID)).willReturn(constructionModel);

        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);

        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(buildingConstructionToQueueItemConverter.convert(building)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, planet)).willReturn(queueResponse);

        underTest.updateConstruction(syncCache, game, planet, CONSTRUCTION_ID, COMPLETED_WORK_POINTS);

        verify(construction).setCurrentWorkPoints(CURRENT_WORK_POINTS + COMPLETED_WORK_POINTS);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED), eq(SURFACE_ID), argumentCaptor.capture());
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED), eq(CONSTRUCTION_ID), argumentCaptor.capture());
        argumentCaptor.getAllValues()
            .forEach(Runnable::run);
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);

    }
}