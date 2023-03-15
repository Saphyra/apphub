package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
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
public class AllocatedResourceAmountQueryServiceTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer AMOUNT = 234;

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private AllocatedResourceAmountQueryService underTest;

    @Mock
    private AllocatedResource allocatedResource1;

    @Mock
    private AllocatedResource allocatedResource2;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ResourceData resourceData;

    @BeforeEach
    public void setUp() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
    }

    @Test
    public void byDataIdAndPlanet() {
        given(storageDetails.getAllocatedResources()).willReturn(new AllocatedResources(Arrays.asList(allocatedResource1, allocatedResource2)));
        given(allocatedResource1.getDataId()).willReturn(DATA_ID_1);
        given(allocatedResource2.getDataId()).willReturn(DATA_ID_2);
        given(allocatedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getAllocatedResourceAmount(DATA_ID_1, planet);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void byPlanetAndStorageType() {
        given(storageDetails.getAllocatedResources()).willReturn(new AllocatedResources(Arrays.asList(allocatedResource1, allocatedResource2)));
        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(resourceData.getId()).willReturn(DATA_ID_1);

        given(allocatedResource1.getDataId()).willReturn(DATA_ID_1);
        given(allocatedResource2.getDataId()).willReturn(DATA_ID_2);
        given(allocatedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getAllocatedResourceAmount(planet, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT);
    }
}