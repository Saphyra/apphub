package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.TestStoredResourcesFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ActualResourceAmountQueryServiceTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer AMOUNT = 245;
    private static final Integer MASS = 246;
    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private ActualResourceAmountQueryService underTest;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Mock
    private ResourceData resourceData;

    @Mock
    private StoredResources storedResources;

    @Test
    public void getActualAmount_byDataIdAndPlanet() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStoredResources()).willReturn(storedResources);
        given(storedResources.get(DATA_ID_1)).willReturn(storedResource1);
        given(storedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getActualAmount(DATA_ID_1, planet);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void getActualAmount_byPlanetAndStorageType() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStoredResources()).willReturn(storedResources);

        given(storedResources.values()).willReturn(List.of(storedResource1, storedResource2));

        given(storedResource1.getDataId()).willReturn(DATA_ID_1);
        given(storedResource2.getDataId()).willReturn(DATA_ID_2);

        given(resourceData.getId()).willReturn(DATA_ID_1);
        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(storedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getActualAmount(planet, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void getActualStorageAmount() {
        storedResources = TestStoredResourcesFactory.create();

        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStoredResources()).willReturn(storedResources);

        storedResources.put(DATA_ID_1, storedResource1);
        storedResources.put(DATA_ID_2, storedResource2);

        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(storedResource1.getDataId()).willReturn(DATA_ID_1);
        given(storedResource1.getAmount()).willReturn(AMOUNT);
        given(resourceData.getId()).willReturn(DATA_ID_1);
        given(resourceData.getMass()).willReturn(MASS);
        given(resourceDataService.get(DATA_ID_1)).willReturn(resourceData);

        int result = underTest.getActualStorageAmount(planet, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT * MASS);
    }
}