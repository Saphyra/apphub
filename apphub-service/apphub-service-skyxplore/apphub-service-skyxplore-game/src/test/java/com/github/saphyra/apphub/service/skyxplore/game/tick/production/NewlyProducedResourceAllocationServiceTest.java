package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NewlyProducedResourceAllocationServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 324;
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private TickCache tickCache;

    @Mock
    private ReservedStorageToModelConverter reservedStorageToModelConverter;

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    @InjectMocks
    private NewlyProducedResourceAllocationService underTest;

    @Mock
    private Planet planet;

    @Mock
    private ProductionOrder order;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private StorageDetails storageDetails;

    @Test
    public void allocateNewlyProducedResource() {
        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);

        given(reservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(reservedStorage.getDataId()).willReturn(DATA_ID);

        AllocatedResources allocatedResources = new AllocatedResources(List.of(allocatedResource));
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);

        given(allocatedResource.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResource.getDataId()).willReturn(DATA_ID);

        given(order.getAmount()).willReturn(AMOUNT);

        given(reservedStorageToModelConverter.convert(reservedStorage, GAME_ID)).willReturn(reservedStorageModel);
        given(allocatedResourceToModelConverter.convert(allocatedResource, GAME_ID)).willReturn(allocatedResourceModel);

        underTest.allocateNewlyProducedResource(GAME_ID, planet, order, reservedStorage);

        verify(reservedStorage).decreaseAmount(AMOUNT);
        verify(gameItemCache).save(reservedStorageModel);

        verify(allocatedResource).increaseAmount(AMOUNT);
        verify(gameItemCache).save(allocatedResourceModel);
    }
}