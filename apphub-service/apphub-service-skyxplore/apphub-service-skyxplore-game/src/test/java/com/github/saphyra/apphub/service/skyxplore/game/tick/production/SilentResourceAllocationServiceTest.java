package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AvailableResourceCounter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SilentResourceAllocationServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer REQUESTED_AMOUNT = 134;
    private static final Integer AVAILABLE_AMOUNT = 30;
    private static final int RESERVED_AMOUNT = REQUESTED_AMOUNT - AVAILABLE_AMOUNT;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceFactory allocatedResourceFactory;

    @Mock
    private AvailableResourceCounter availableResourceCounter;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @Mock
    private TickCache tickCache;

    @Mock
    private ReservedStorageToModelConverter reservedStorageToModelConverter;

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @InjectMocks
    private SilentResourceAllocationService underTest;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private MessageCache messageCache;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Test
    public void allocateResources() {
        ReservedStorages reservedStorages = new ReservedStorages();
        AllocatedResources allocatedResources = new AllocatedResources();
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(planet.getOwner()).willReturn(USER_ID);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);
        given(tickCacheItem.getMessageCache()).willReturn(messageCache);

        given(availableResourceCounter.countAvailableAmount(storageDetails, DATA_ID)).willReturn(AVAILABLE_AMOUNT);

        given(allocatedResourceFactory.create(PLANET_ID, LocationType.PRODUCTION, EXTERNAL_REFERENCE, DATA_ID, AVAILABLE_AMOUNT)).willReturn(allocatedResource);
        given(reservedStorageFactory.create(PLANET_ID, LocationType.PRODUCTION, EXTERNAL_REFERENCE, DATA_ID, RESERVED_AMOUNT)).willReturn(reservedStorage);

        given(allocatedResourceToModelConverter.convert(allocatedResource, GAME_ID)).willReturn(allocatedResourceModel);
        given(reservedStorageToModelConverter.convert(reservedStorage, GAME_ID)).willReturn(reservedStorageModel);
        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(planetStorageResponse);

        List<ReservedStorage> result = underTest.allocateResources(GAME_ID, planet, EXTERNAL_REFERENCE, CollectionUtils.singleValueMap(DATA_ID, REQUESTED_AMOUNT));

        assertThat(reservedStorages).containsExactly(reservedStorage);
        assertThat(allocatedResources).containsExactly(allocatedResource);

        assertThat(result).containsExactly(reservedStorage);

        verify(gameItemCache).saveAll(List.of(reservedStorageModel, allocatedResourceModel));

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue().run();
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);
    }
}