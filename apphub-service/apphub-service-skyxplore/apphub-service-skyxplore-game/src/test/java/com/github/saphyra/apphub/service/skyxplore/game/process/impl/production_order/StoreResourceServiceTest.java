package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreResourceServiceTest {
    private static final int AMOUNT = 24;
    private static final String DATA_ID = "data-id";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceConverter allocatedResourceConverter;

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @InjectMocks
    private StoreResourceService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Test
    public void storeResource() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(PLANET_ID, DATA_ID)).willReturn(Optional.of(storedResource));
        given(reservedStorage.getDataId()).willReturn(DATA_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(allocatedResourceConverter.toModel(GAME_ID, allocatedResource)).willReturn(allocatedResourceModel);

        underTest.storeResource(syncCache, gameData, PLANET_ID, USER_ID, reservedStorage, allocatedResource, AMOUNT);

        verify(allocatedResource).increaseAmount(AMOUNT);
        verify(storedResource).increaseAmount(AMOUNT);
        verify(reservedStorage).decreaseAmount(AMOUNT);

        verify(syncCache).saveGameItem(allocatedResourceModel);

        syncCache.resourceStored(USER_ID, PLANET_ID, storedResource, reservedStorage);
    }

    @Test
    public void storeResource_noStoredResource() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(PLANET_ID, DATA_ID)).willReturn(Optional.empty());
        given(storedResourceFactory.create(PLANET_ID, DATA_ID)).willReturn(storedResource);
        given(reservedStorage.getDataId()).willReturn(DATA_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(allocatedResourceConverter.toModel(GAME_ID, allocatedResource)).willReturn(allocatedResourceModel);

        underTest.storeResource(syncCache, gameData, PLANET_ID, USER_ID, reservedStorage, allocatedResource, AMOUNT);

        verify(allocatedResource).increaseAmount(AMOUNT);
        verify(storedResource).increaseAmount(AMOUNT);
        verify(reservedStorage).decreaseAmount(AMOUNT);

        verify(syncCache).saveGameItem(allocatedResourceModel);

        syncCache.resourceStored(USER_ID, PLANET_ID, storedResource, reservedStorage);
    }
}