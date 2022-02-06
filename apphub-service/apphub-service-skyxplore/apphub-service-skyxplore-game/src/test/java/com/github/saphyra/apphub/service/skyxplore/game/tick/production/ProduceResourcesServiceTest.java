package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProduceResourcesServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 214;

    @Mock
    private ProductionOrderFactory productionOrderFactory;

    @Mock
    private ProductionOrderProcessingService productionOrderProcessingService;

    @Mock
    private TickCache tickCache;

    @Mock
    private ProductionOrderToModelConverter productionOrderToModelConverter;

    @InjectMocks
    private ProduceResourcesService underTest;

    @Mock
    private ReservedStorage anotherReservedStorage;

    @Mock
    private ReservedStorage completedReservedStorage;

    @Mock
    private ReservedStorage toBeDoneReservedStorage;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ProductionOrder order;

    @Mock
    private ProductionOrderModel productionOrderModel;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Test
    public void produceResources() {
        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);

        Set<ProductionOrder> orders = new HashSet<>();
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOrders()).willReturn(orders);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(List.of(anotherReservedStorage, completedReservedStorage, toBeDoneReservedStorage)));

        given(anotherReservedStorage.getReservedStorageId()).willReturn(UUID.randomUUID());
        given(anotherReservedStorage.getExternalReference()).willReturn(UUID.randomUUID());

        given(completedReservedStorage.getReservedStorageId()).willReturn(UUID.randomUUID());
        given(completedReservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(completedReservedStorage.getAmount()).willReturn(0);

        given(toBeDoneReservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(toBeDoneReservedStorage.getAmount()).willReturn(AMOUNT);
        given(toBeDoneReservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(toBeDoneReservedStorage.getDataId()).willReturn(DATA_ID);

        given(productionOrderFactory.create(RESERVED_STORAGE_ID, PLANET_ID, LocationType.PLANET, DATA_ID, AMOUNT)).willReturn(order);
        given(productionOrderToModelConverter.convert(order, GAME_ID)).willReturn(productionOrderModel);

        underTest.produceResources(GAME_ID, planet, EXTERNAL_REFERENCE);

        assertThat(orders).containsExactlyInAnyOrder(order);

        verify(gameItemCache).save(productionOrderModel);

        verify(productionOrderProcessingService).processOrder(GAME_ID, planet, order);
    }
}