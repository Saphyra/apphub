package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;

@RunWith(MockitoJUnitRunner.class)
public class FreeStorageQueryServiceTest {
    private static final String DATA_ID = "data-id";
    private static final Integer ACTUAL_AMOUNT = 4325;
    private static final Integer ALLOCATED_AMOUNT = 23;
    private static final Integer CAPACITY = 324234;
    private static final Integer RESERVED_STORAGE = 234;

    @Mock
    private StorageCalculator storageCalculator;

    @Mock
    private ActualResourceAmountQueryService actualResourceAmountQueryService;

    @Mock
    private AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;

    @Mock
    private ReservedStorageQueryService reservedStorageQueryService;

    @InjectMocks
    private FreeStorageQueryService underTest;

    @Mock
    private Planet planet;

    @Test
    public void getUsableStoredResourceAmount() {
        given(actualResourceAmountQueryService.getActualAmount(DATA_ID, planet)).willReturn(ACTUAL_AMOUNT);
        given(allocatedResourceAmountQueryService.getAllocatedResourceAmount(DATA_ID, planet)).willReturn(ALLOCATED_AMOUNT);

        int result = underTest.getUsableStoredResourceAmount(DATA_ID, planet);

        assertThat(result).isEqualTo(ACTUAL_AMOUNT - ALLOCATED_AMOUNT);
    }

    @Test
    public void getFreeStorage() {
        given(storageCalculator.calculateCapacity(planet, StorageType.BULK)).willReturn(CAPACITY);
        given(actualResourceAmountQueryService.getActualAmount(planet, StorageType.BULK)).willReturn(ACTUAL_AMOUNT);
        given(reservedStorageQueryService.getReservedStorageAmount(planet, StorageType.BULK)).willReturn(RESERVED_STORAGE);

        int result = underTest.getFreeStorage(planet, StorageType.BULK);

        assertThat(result).isEqualTo(CAPACITY - ACTUAL_AMOUNT - RESERVED_STORAGE);
    }
}