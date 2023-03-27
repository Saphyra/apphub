package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
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
public class AllocationRemovalServiceTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @InjectMocks
    private AllocationRemovalService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private ReservedStorages reservedStorages;

    @Test
    public void removeAllocationsAndReservations() {
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);

        given(allocatedResources.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(allocatedResource));
        given(reservedStorages.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(reservedStorage));

        given(allocatedResource.getAllocatedResourceId()).willReturn(ALLOCATED_RESOURCE_ID);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        given(planetStorageOverviewQueryService.getStorage(gameData, LOCATION)).willReturn(planetStorageResponse);

        underTest.removeAllocationsAndReservations(syncCache, gameData, LOCATION, USER_ID, EXTERNAL_REFERENCE);

        verify(syncCache).deleteGameItem(ALLOCATED_RESOURCE_ID, GameItemType.ALLOCATED_RESOURCE);
        verify(syncCache).deleteGameItem(RESERVED_STORAGE_ID, GameItemType.RESERVED_STORAGE);
        verify(allocatedResources).remove(allocatedResource);
        verify(reservedStorages).remove(reservedStorage);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(LOCATION), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetStorageModified(USER_ID, LOCATION, planetStorageResponse);
    }
}