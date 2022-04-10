package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
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
public class StoreResourceServiceTest {
    private static final int AMOUNT = 24;
    private static final String DATA_ID = "data-id";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private ReservedStorageToModelConverter reservedStorageToModelConverter;

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    @Mock
    private StoredResourceToModelConverter storedResourceToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @InjectMocks
    private StoreResourceService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Test
    public void storeResource() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStoredResources()).willReturn(storedResources);
        given(reservedStorage.getDataId()).willReturn(DATA_ID);
        given(storedResources.get(DATA_ID)).willReturn(storedResource);

        given(game.getGameId()).willReturn(GAME_ID);
        given(storedResourceToModelConverter.convert(storedResource, GAME_ID)).willReturn(storedResourceModel);
        given(allocatedResourceToModelConverter.convert(allocatedResource, GAME_ID)).willReturn(allocatedResourceModel);
        given(reservedStorageToModelConverter.convert(reservedStorage, GAME_ID)).willReturn(reservedStorageModel);

        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(planetStorageResponse);

        underTest.storeResource(syncCache, game, planet, reservedStorage, allocatedResource, AMOUNT);

        verify(allocatedResource).increaseAmount(AMOUNT);
        verify(storedResource).increaseAmount(AMOUNT);
        verify(reservedStorage).decreaseAmount(AMOUNT);

        verify(syncCache).saveGameItem(storedResourceModel);
        verify(syncCache).saveGameItem(allocatedResourceModel);
        verify(syncCache).saveGameItem(reservedStorageModel);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);
    }
}