package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @InjectMocks
    private AllocationRemovalService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Test
    public void removeAllocationsAndReservations() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);

        given(allocatedResources.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(allocatedResource));
        given(reservedStorages.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(reservedStorage));

        given(allocatedResource.getAllocatedResourceId()).willReturn(ALLOCATED_RESOURCE_ID);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(planetStorageResponse);

        underTest.removeAllocationsAndReservations(syncCache, planet, EXTERNAL_REFERENCE);

        verify(syncCache).deleteGameItem(ALLOCATED_RESOURCE_ID, GameItemType.ALLOCATED_RESOURCE);
        verify(syncCache).deleteGameItem(RESERVED_STORAGE_ID, GameItemType.RESERVED_STORAGE);
        verify(allocatedResources).remove(allocatedResource);
        verify(reservedStorages).remove(reservedStorage);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);
    }
}