package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UseAllocatedResourceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 234;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    @Mock
    private StoredResourceToModelConverter storedResourceToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @InjectMocks
    private UseAllocatedResourceService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private StoredResources storedResources;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Test
    public void resolveAllocations() {
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(reservedStorage));
        given(reservedStorage.getDataId()).willReturn(DATA_ID);

        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResources.findByExternalReferenceAndDataIdValidated(EXTERNAL_REFERENCE, DATA_ID)).willReturn(allocatedResource);
        given(allocatedResource.getAmount()).willReturn(AMOUNT);

        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataIdOrDefault(PLANET_ID, DATA_ID)).willReturn(storedResource);

        given(allocatedResourceToModelConverter.convert(GAME_ID, allocatedResource)).willReturn(allocatedResourceModel);
        given(storedResourceToModelConverter.convert(GAME_ID, storedResource)).willReturn(storedResourceModel);
        given(planetStorageOverviewQueryService.getStorage(gameData, PLANET_ID)).willReturn(planetStorageResponse);

        underTest.resolveAllocations(syncCache, gameData, PLANET_ID, USER_ID, EXTERNAL_REFERENCE);

        verify(storedResource).decreaseAmount(AMOUNT);
        verify(allocatedResource).setAmount(0);

        verify(syncCache).saveGameItem(allocatedResourceModel);
        verify(syncCache).saveGameItem(storedResourceModel);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);
    }
}