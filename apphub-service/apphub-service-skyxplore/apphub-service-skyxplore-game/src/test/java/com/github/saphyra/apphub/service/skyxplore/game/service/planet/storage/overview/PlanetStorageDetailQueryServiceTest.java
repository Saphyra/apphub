package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ActualResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocatedResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;

@ExtendWith(MockitoExtension.class)
public class PlanetStorageDetailQueryServiceTest {
    private static final Integer CAPACITY = 24;
    private static final Integer RESERVED_STORAGE_AMOUNT = 235;
    private static final Integer ACTUAL_AMOUNT = 2452;
    private static final Integer ALLOCATED_AMOUNT = 42542;

    @Mock
    private  StorageCalculator storageCalculator;

    @Mock
    private  ReservedStorageQueryService reservedStorageQueryService;

    @Mock
    private  ActualResourceAmountQueryService actualResourceAmountQueryService;

    @Mock
    private  AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;

    @Mock
    private  ResourceDetailsQueryService resourceDetailsQueryService;

    @InjectMocks
    private PlanetStorageDetailQueryService underTest;

    @Mock
    private Planet planet;

    @Mock
    private ResourceDetailsResponse resourceDetailsResponse;

    @Test
    public void getStorageDetails(){
        given(storageCalculator.calculateCapacity(planet, StorageType.BULK)).willReturn(CAPACITY);
        given(reservedStorageQueryService.getReservedAmount(planet, StorageType.BULK)).willReturn(RESERVED_STORAGE_AMOUNT);
        given(actualResourceAmountQueryService.getActualAmount(planet, StorageType.BULK)).willReturn(ACTUAL_AMOUNT);
        given(allocatedResourceAmountQueryService.getAllocatedResourceAmount(planet, StorageType.BULK)).willReturn(ALLOCATED_AMOUNT);
        given(resourceDetailsQueryService.getResourceDetails(planet, StorageType.BULK)).willReturn(Arrays.asList(resourceDetailsResponse));

        StorageDetailsResponse result = underTest.getStorageDetails(planet, StorageType.BULK);

        assertThat(result.getCapacity()).isEqualTo(CAPACITY);
        assertThat(result.getReservedStorageAmount()).isEqualTo(RESERVED_STORAGE_AMOUNT);
        assertThat(result.getActualResourceAmount()).isEqualTo(ACTUAL_AMOUNT);
        assertThat(result.getAllocatedResourceAmount()).isEqualTo(ALLOCATED_AMOUNT);
        assertThat(result.getResourceDetails()).containsExactly(resourceDetailsResponse);
    }
}