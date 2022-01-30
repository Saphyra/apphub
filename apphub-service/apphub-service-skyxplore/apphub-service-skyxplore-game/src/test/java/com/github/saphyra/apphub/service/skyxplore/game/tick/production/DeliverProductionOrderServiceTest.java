package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeliverProductionOrderServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final int AMOUNT = 3;
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private TickCache tickCache;

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @Mock
    private StoredResourceToModelConverter storedResourceToModelConverter;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private NewlyProducedResourceAllocationService newlyProducedResourceAllocationService;

    @InjectMocks
    private DeliverProductionOrderService underTest;

    @Mock
    private Planet planet;

    @Mock
    private ProductionOrder order;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StoredResource storedResource;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private MessageCache messageCache;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Test
    public void deliverOrder() {
        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);
        given(tickCacheItem.getMessageCache()).willReturn(messageCache);

        Map<String, StoredResource> storedResources = new HashMap<>();
        Set<ProductionOrder> orders = CollectionUtils.toSet(order);
        ReservedStorages reservedStorages = new ReservedStorages(List.of(reservedStorage));
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(planet.getOrders()).willReturn(orders);
        given(storageDetails.getStoredResources()).willReturn(storedResources);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);

        given(order.getProductionOrderId()).willReturn(PRODUCTION_ORDER_ID);
        given(order.getDataId()).willReturn(DATA_ID);
        given(order.getAmount()).willReturn(AMOUNT);
        given(order.getExternalReference()).willReturn(RESERVED_STORAGE_ID);

        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        given(storedResourceFactory.create(PLANET_ID, LocationType.PLANET, DATA_ID, 0)).willReturn(storedResource);

        given(storedResourceToModelConverter.convert(storedResource, GAME_ID)).willReturn(storedResourceModel);
        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(planetStorageResponse);

        underTest.deliverOrder(GAME_ID, planet, order);

        verify(storedResource).increaseAmount(AMOUNT);
        verify(gameItemCache).save(storedResourceModel);
        verify(newlyProducedResourceAllocationService).allocateNewlyProducedResource(GAME_ID, planet, order, reservedStorage);
        assertThat(orders).isEmpty();
        verify(gameItemCache).delete(PRODUCTION_ORDER_ID, GameItemType.PRODUCTION_ORDER);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue().run();
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);
    }
}