package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
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
public class AllocatedResourceResolverTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int ALLOCATED_AMOUNT = 1423;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private StoredResourceToModelConverter storedResourceToModelConverter;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private TickCache tickCache;

    @InjectMocks
    private AllocatedResourceResolver underTest;

    @Mock
    private Planet planet;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private MessageCache messageCache;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Test
    public void resolveAllocations() {
        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);
        given(tickCacheItem.getMessageCache()).willReturn(messageCache);

        StorageDetails storageDetails = StorageDetails.builder()
            .reservedStorages(new ReservedStorages(List.of(reservedStorage)))
            .allocatedResources(new AllocatedResources(List.of(allocatedResource)))
            .storedResources(CollectionUtils.singleValueMap(DATA_ID, storedResource))
            .build();
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(reservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        given(allocatedResource.getAllocatedResourceId()).willReturn(ALLOCATED_RESOURCE_ID);
        given(allocatedResource.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResource.getAmount()).willReturn(ALLOCATED_AMOUNT);
        given(allocatedResource.getDataId()).willReturn(DATA_ID);

        given(storedResourceToModelConverter.convert(storedResource, GAME_ID)).willReturn(storedResourceModel);
        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(planetStorageResponse);

        underTest.resolveAllocations(GAME_ID, planet, EXTERNAL_REFERENCE);

        verify(gameItemCache).delete(RESERVED_STORAGE_ID, GameItemType.RESERVED_STORAGE);
        verify(gameItemCache).delete(ALLOCATED_RESOURCE_ID, GameItemType.ALLOCATED_RESOURCE);
        verify(gameItemCache).save(storedResourceModel);

        verify(storedResource).decreaseAmount(ALLOCATED_AMOUNT);

        assertThat(storageDetails.getReservedStorages()).isEmpty();

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue().run();
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);
    }
}