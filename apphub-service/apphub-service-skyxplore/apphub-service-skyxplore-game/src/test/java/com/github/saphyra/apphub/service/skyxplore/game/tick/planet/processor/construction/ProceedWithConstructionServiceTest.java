package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AssignCitizenService;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AvailableCitizenProvider;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.MakeCitizenWorkService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProceedWithConstructionServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 143;
    private static final Integer CURRENT_WORK_POINTS = REQUIRED_WORK_POINTS - 1;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer COMPLETED_WORK = 2;
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private AvailableCitizenProvider availableCitizenProvider;

    @Mock
    private TickCache tickCache;

    @Mock
    private AssignCitizenService assignCitizenService;

    @Mock
    private MakeCitizenWorkService makeCitizenWorkService;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private BuildingConstructionToQueueItemConverter buildingConstructionToQueueItemConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @InjectMocks
    private ProceedWithConstructionService underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction construction;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private Assignment assignment;

    @Mock
    private Citizen citizen;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private MessageCache messageCache;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private Building building;

    @Test
    public void proceedWithConstruction() {
        Map<UUID, Assignment> assignments = CollectionUtils.singleValueMap(CITIZEN_ID, assignment);

        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);
        given(tickCacheItem.getMessageCache()).willReturn(messageCache);
        given(tickCacheItem.getCitizenAssignments()).willReturn(assignments);

        OptionalMap<UUID, Citizen> population = new OptionalHashMap<>(CollectionUtils.singleValueMap(CITIZEN_ID, citizen));
        given(planet.getPopulation()).willReturn(population);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surface.getBuilding()).willReturn(building);

        given(construction.getParallelWorkers()).willReturn(2);
        given(construction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);

        given(availableCitizenProvider.findMostCapableUnemployedCitizen(assignments, population.values(), CONSTRUCTION_ID, SkillType.BUILDING))
            .willReturn(Optional.of(citizen))
            .willReturn(Optional.empty());

        given(assignCitizenService.assignCitizen(GAME_ID, citizen, CONSTRUCTION_ID)).willReturn(assignment);
        given(makeCitizenWorkService.requestWork(GAME_ID, USER_ID, PLANET_ID, assignment, 1, SkillType.BUILDING)).willReturn(COMPLETED_WORK);

        given(constructionToModelConverter.convert(construction, GAME_ID)).willReturn(constructionModel);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(buildingConstructionToQueueItemConverter.convert(building)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, planet)).willReturn(queueResponse);

        underTest.proceedWithConstruction(GAME_ID, planet, surface, construction);

        verify(construction, times(1)).setCurrentWorkPoints(CURRENT_WORK_POINTS + COMPLETED_WORK);
        verify(gameItemCache).save(constructionModel);

        ArgumentCaptor<Runnable> surfaceModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED), eq(SURFACE_ID), surfaceModifiedArgumentCaptor.capture());
        surfaceModifiedArgumentCaptor.getValue().run();
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);

        ArgumentCaptor<Runnable> queueItemModifiedArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED), eq(CONSTRUCTION_ID), queueItemModifiedArgumentCaptor.capture());
        queueItemModifiedArgumentCaptor.getValue().run();
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);
    }
}