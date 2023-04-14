package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AvailableResourceCounter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductionRequirementsAllocationServiceTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 3214;
    private static final Integer AVAILABLE_AMOUNT = 123;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private AvailableResourceCounter availableResourceCounter;

    @Mock
    private AllocatedResourceFactory allocatedResourceFactory;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @InjectMocks
    private ProductionRequirementsAllocationService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private AllocatedResources allocatedResources;

    @Test
    public void allocate() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);

        given(availableResourceCounter.countAvailableAmount(gameData, PLANET_ID, DATA_ID)).willReturn(AVAILABLE_AMOUNT);
        given(allocatedResourceFactory.create(PLANET_ID, EXTERNAL_REFERENCE, DATA_ID, AVAILABLE_AMOUNT)).willReturn(allocatedResource);
        given(reservedStorageFactory.create(PLANET_ID, EXTERNAL_REFERENCE, DATA_ID, AMOUNT - AVAILABLE_AMOUNT)).willReturn(reservedStorage);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        UUID result = underTest.allocate(syncCache, gameData, PLANET_ID, USER_ID, EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        assertThat(result).isEqualTo(RESERVED_STORAGE_ID);

        verify(allocatedResources).add(allocatedResource);
        verify(reservedStorages).add(reservedStorage);

        verify(syncCache).resourceAllocated(USER_ID, PLANET_ID, allocatedResource, reservedStorage);
    }
}