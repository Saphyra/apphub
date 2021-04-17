package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AvailableStorageMapperTest {
    private static final String DATA_ID = "data-id";
    private static final Integer STORED_AMOUNT = 45;
    private static final Integer RESERVED_AMOUNT = 23;
    private static final Integer CAPACITY = 100;

    @Mock
    private StoredResourceCounter storedResourceCounter;

    @Mock
    private ReservedStorageCounter reservedStorageCounter;

    @Mock
    private StorageCalculator storageCalculator;

    @InjectMocks
    private AvailableStorageMapper underTest;

    @Mock
    private Building building;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StoredResource storedResource;

    @Mock
    private ReservedStorages reservedStorages;

    @Test
    public void countAvailableStorage() {
        given(storageDetails.getStoredResources()).willReturn(CollectionUtils.singleValueMap(DATA_ID, storedResource));
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(storedResourceCounter.countStoredResources(any(), eq(CollectionUtils.singleValueMap(DATA_ID, storedResource)))).willReturn(STORED_AMOUNT);
        given(reservedStorageCounter.countReservedStorage(any(), eq(reservedStorages))).willReturn(RESERVED_AMOUNT);
        given(storageCalculator.calculateCapacity(any(), eq(Arrays.asList(building)))).willReturn(CAPACITY);

        Map<String, Integer> result = underTest.countAvailableStorage(Arrays.asList(building), storageDetails);

        Arrays.stream(StorageType.values())
            .forEach(storageType -> assertThat(result).containsEntry(storageType.name(), CAPACITY - STORED_AMOUNT - RESERVED_AMOUNT));
    }
}