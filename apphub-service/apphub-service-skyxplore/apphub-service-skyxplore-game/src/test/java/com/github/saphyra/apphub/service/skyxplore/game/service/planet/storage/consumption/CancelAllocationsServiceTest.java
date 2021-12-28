package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CancelAllocationsServiceTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private CancelAllocationsService underTest;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private Planet planet;

    @Test
    public void cancelAllocations() {
        given(planet.getStorageDetails()).willReturn(storageDetails);

        AllocatedResources allocatedResources = new AllocatedResources(List.of(allocatedResource));
        ReservedStorages reservedStorages = new ReservedStorages(List.of(reservedStorage));

        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);

        given(allocatedResource.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResource.getAllocatedResourceId()).willReturn(ALLOCATED_RESOURCE_ID);

        given(reservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        underTest.cancelAllocations(planet, EXTERNAL_REFERENCE);

        assertThat(allocatedResources).isEmpty();
        assertThat(reservedStorages).isEmpty();

        verify(gameDataProxy).deleteItem(ALLOCATED_RESOURCE_ID, GameItemType.ALLOCATED_RESOURCE);
        verify(gameDataProxy).deleteItem(RESERVED_STORAGE_ID, GameItemType.RESERVED_STORAGE);
    }
}