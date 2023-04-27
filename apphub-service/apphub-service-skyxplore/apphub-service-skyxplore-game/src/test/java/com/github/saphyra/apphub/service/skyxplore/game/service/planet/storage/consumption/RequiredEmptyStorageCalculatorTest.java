package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RequiredEmptyStorageCalculatorTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer AMOUNT = 324;
    private static final Integer MASS = 245;

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private RequiredEmptyStorageCalculator underTest;

    @Mock
    private ConsumptionResult consumptionResult1;

    @Mock
    private ConsumptionResult consumptionResult2;

    @Mock
    private ReservedStorage reservedStorage1;

    @Mock
    private ReservedStorage reservedStorage2;

    @Mock
    private ResourceData resourceData1;

    @Mock
    private ResourceData resourceData2;


    @Test
    public void getRequiredStorageAmount() {
        Map<String, ConsumptionResult> consumptions = Map.of(
            DATA_ID_1, consumptionResult1,
            DATA_ID_2, consumptionResult2
        );

        given(consumptionResult1.getReservation()).willReturn(reservedStorage1);
        given(consumptionResult2.getReservation()).willReturn(reservedStorage2);

        given(reservedStorage1.getDataId()).willReturn(DATA_ID_1);
        given(reservedStorage2.getDataId()).willReturn(DATA_ID_2);

        given(resourceDataService.get(DATA_ID_1)).willReturn(resourceData1);
        given(resourceDataService.get(DATA_ID_2)).willReturn(resourceData2);

        given(resourceData1.getStorageType()).willReturn(StorageType.CITIZEN);
        given(resourceData2.getStorageType()).willReturn(StorageType.BULK);
        given(resourceData2.getMass()).willReturn(MASS);

        given(reservedStorage2.getAmount()).willReturn(AMOUNT);

        int result = underTest.getRequiredStorageAmount(StorageType.BULK, consumptions);

        assertThat(result).isEqualTo(MASS * AMOUNT);
    }
}