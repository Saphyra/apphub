package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageBuildingDataValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingDataValidator buildingDataValidator;

    @InjectMocks
    private StorageBuildingValidator underTest;

    @Mock
    private StorageBuildingData storageBuildingData;

    @AfterEach
    public void validate() {
        verify(buildingDataValidator).validate(storageBuildingData);
    }

    @Test
    public void nullStores() {
        Map<String, StorageBuildingData> map = new HashMap<>();
        map.put(KEY, storageBuildingData);
        given(storageBuildingData.getStores()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullCapacity() {
        Map<String, StorageBuildingData> map = new HashMap<>();
        map.put(KEY, storageBuildingData);
        given(storageBuildingData.getStores()).willReturn(StorageType.BULK);
        given(storageBuildingData.getCapacity()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void tooLowCapacity() {
        Map<String, StorageBuildingData> map = new HashMap<>();
        map.put(KEY, storageBuildingData);
        given(storageBuildingData.getStores()).willReturn(StorageType.BULK);
        given(storageBuildingData.getCapacity()).willReturn(0);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void valid() {
        Map<String, StorageBuildingData> map = new HashMap<>();
        map.put(KEY, storageBuildingData);
        given(storageBuildingData.getStores()).willReturn(StorageType.BULK);
        given(storageBuildingData.getCapacity()).willReturn(2);

        underTest.validate(map);
    }
}