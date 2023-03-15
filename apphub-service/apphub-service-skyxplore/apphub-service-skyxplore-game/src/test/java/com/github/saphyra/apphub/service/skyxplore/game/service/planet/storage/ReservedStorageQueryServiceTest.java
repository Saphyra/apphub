package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReservedStorageQueryServiceTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer AMOUNT = 3214;
    private static final Integer MASS = 25;
    private static final String DATA_ID_3 = "data-id-3";

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private ReservedStorageQueryService underTest;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ReservedStorage reservedStorage1;

    @Mock
    private ReservedStorage reservedStorage2;

    @Mock
    private ReservedStorage reservedStorage3;

    @Mock
    private ResourceData resourceData;

    @BeforeEach
    public void setUp() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
    }

    @Test
    public void getReservedAmount_byDataIdAndPlanet() {
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(Arrays.asList(reservedStorage1, reservedStorage2)));
        given(reservedStorage1.getDataId()).willReturn(DATA_ID_1);
        given(reservedStorage2.getDataId()).willReturn(DATA_ID_2);
        given(reservedStorage1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getReservedAmount(DATA_ID_1, planet);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void getReservedAmount_byPlanetAndStorageType() {
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(Arrays.asList(reservedStorage1, reservedStorage2, reservedStorage3)));
        given(resourceData.getId()).willReturn(DATA_ID_1);
        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(reservedStorage1.getDataId()).willReturn(DATA_ID_1);
        given(reservedStorage1.getAmount()).willReturn(AMOUNT);
        given(reservedStorage2.getDataId()).willReturn(DATA_ID_2);
        given(reservedStorage3.getDataId()).willReturn(DATA_ID_1);
        given(reservedStorage3.getLocationType()).willReturn(LocationType.PRODUCTION);

        int result = underTest.getReservedAmount(planet, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void getReservedStorageCapacity() {
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(Arrays.asList(reservedStorage1, reservedStorage2, reservedStorage3)));
        given(reservedStorage1.getDataId()).willReturn(DATA_ID_1);
        given(reservedStorage2.getDataId()).willReturn(DATA_ID_2);
        given(reservedStorage1.getAmount()).willReturn(AMOUNT);
        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(resourceData.getId()).willReturn(DATA_ID_1);
        given(resourceData.getMass()).willReturn(MASS);
        given(resourceDataService.get(DATA_ID_1)).willReturn(resourceData);
        given(reservedStorage3.getDataId()).willReturn(DATA_ID_1);
        given(reservedStorage3.getLocationType()).willReturn(LocationType.PRODUCTION);

        int result = underTest.getReservedStorageCapacity(planet, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT * MASS);
    }
}