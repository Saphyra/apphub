package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataValidator;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageBuildingValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingDataValidator buildingDataValidator;

    @InjectMocks
    private StorageBuildingValidator underTest;

    @Mock
    private StorageBuilding storageBuilding;

    @After
    public void validate() {
        verify(buildingDataValidator).validate(storageBuilding);
    }

    @Test(expected = IllegalStateException.class)
    public void nullStores() {
        Map<String, StorageBuilding> map = new HashMap<>();
        map.put(KEY, storageBuilding);
        given(storageBuilding.getStores()).willReturn(null);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void nullCapacity() {
        Map<String, StorageBuilding> map = new HashMap<>();
        map.put(KEY, storageBuilding);
        given(storageBuilding.getStores()).willReturn(StorageType.BULK);
        given(storageBuilding.getCapacity()).willReturn(null);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void tooLowCapacity() {
        Map<String, StorageBuilding> map = new HashMap<>();
        map.put(KEY, storageBuilding);
        given(storageBuilding.getStores()).willReturn(StorageType.BULK);
        given(storageBuilding.getCapacity()).willReturn(0);

        underTest.validate(map);
    }

    @Test
    public void valid() {
        Map<String, StorageBuilding> map = new HashMap<>();
        map.put(KEY, storageBuilding);
        given(storageBuilding.getStores()).willReturn(StorageType.BULK);
        given(storageBuilding.getCapacity()).willReturn(2);

        underTest.validate(map);
    }
}