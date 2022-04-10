package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.service.skyxplore.game.TestStoredResourcesFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ActualResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocatedResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ResourceDetailsResponseMapperTest {
    private static final String DATA_ID = "data-id";
    private static final Integer RESERVED_STORAGE_AMOUNT = 232;
    private static final Integer ACTUAL_AMOUNT = 2552;
    private static final Integer ALLOCATED_AMOUNT = 3563;

    @Mock
    private ReservedStorageQueryService reservedStorageQueryService;

    @Mock
    private ActualResourceAmountQueryService actualResourceAmountQueryService;

    @Mock
    private AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;

    @InjectMocks
    private ResourceDetailsResponseMapper underTest;

    @Mock
    private ResourceData resourceData;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private StorageDetails storageDetails;

    private final StoredResources storedResources = TestStoredResourcesFactory.create();

    @Test
    public void createResourceData() {
        given(resourceData.getId()).willReturn(DATA_ID);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(storageDetails.getStoredResources()).willReturn(storedResources);
        storedResources.put(DATA_ID, storedResource);
        given(storageDetails.getAllocatedResources()).willReturn(new AllocatedResources(Arrays.asList(allocatedResource)));

        given(reservedStorageQueryService.getReservedAmount(DATA_ID, reservedStorages)).willReturn(RESERVED_STORAGE_AMOUNT);
        given(actualResourceAmountQueryService.getActualAmount(DATA_ID, CollectionUtils.singleValueMap(DATA_ID, storedResource))).willReturn(ACTUAL_AMOUNT);
        given(allocatedResourceAmountQueryService.getAllocatedResourceAmount(DATA_ID, Arrays.asList(allocatedResource))).willReturn(ALLOCATED_AMOUNT);

        ResourceDetailsResponse result = underTest.createResourceData(resourceData, storageDetails);

        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getReservedStorageAmount()).isEqualTo(RESERVED_STORAGE_AMOUNT);
        assertThat(result.getActualAmount()).isEqualTo(ACTUAL_AMOUNT);
        assertThat(result.getAllocatedResourceAmount()).isEqualTo(ALLOCATED_AMOUNT);
    }
}