package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AvailableResourceCounterTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer STORED_AMOUNT = 21453;
    private static final Integer ALLOCATED_AMOUNT = 234;

    @InjectMocks
    private AvailableResourceCounter underTest;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResource allocatedResource1;

    @Mock
    private AllocatedResource allocatedResource2;

    @Test
    public void countAvailableAmount() {
        given(storageDetails.getStoredResources()).willReturn(CollectionUtils.singleValueMap(DATA_ID_1, storedResource));
        given(storageDetails.getAllocatedResources()).willReturn(new AllocatedResources(List.of(allocatedResource1, allocatedResource2)));

        given(storedResource.getAmount()).willReturn(STORED_AMOUNT);
        given(allocatedResource1.getAmount()).willReturn(ALLOCATED_AMOUNT);
        given(allocatedResource1.getDataId()).willReturn(DATA_ID_1);
        given(allocatedResource2.getDataId()).willReturn(DATA_ID_2);

        int result = underTest.countAvailableAmount(storageDetails, DATA_ID_1);

        assertThat(result).isEqualTo(STORED_AMOUNT - ALLOCATED_AMOUNT);
    }
}