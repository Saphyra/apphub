package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;


import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FreeStorageQueryServiceTest {
    private static final Integer ACTUAL_AMOUNT = 4325;
    private static final Integer CAPACITY = 324234;
    private static final Integer RESERVED_STORAGE = 234;

    @Mock
    private StorageCalculator storageCalculator;

    @Mock
    private ActualResourceAmountQueryService actualResourceAmountQueryService;

    @Mock
    private ReservedStorageQueryService reservedStorageQueryService;

    @InjectMocks
    private FreeStorageQueryService underTest;

    @Mock
    private Planet planet;

    @Test
    public void getFreeStorage() {
        given(storageCalculator.calculateCapacity(planet, StorageType.BULK)).willReturn(CAPACITY);
        given(actualResourceAmountQueryService.getActualStorageAmount(planet, StorageType.BULK)).willReturn(ACTUAL_AMOUNT);
        given(reservedStorageQueryService.getReservedStorageCapacity(planet, StorageType.BULK)).willReturn(RESERVED_STORAGE);

        int result = underTest.getFreeStorage(planet, StorageType.BULK);

        assertThat(result).isEqualTo(CAPACITY - ACTUAL_AMOUNT - RESERVED_STORAGE);
    }
}