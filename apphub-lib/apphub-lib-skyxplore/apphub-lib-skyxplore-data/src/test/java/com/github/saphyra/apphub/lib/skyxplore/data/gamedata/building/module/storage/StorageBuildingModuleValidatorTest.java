package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StorageBuildingModuleValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingModuleValidator buildingModuleValidator;

    @InjectMocks
    private StorageBuildingModuleValidator underTest;

    @Mock
    private StorageBuildingModuleData buildingModuleData;

    @AfterEach
    void verify() {
        then(buildingModuleValidator).should().validate(buildingModuleData);
    }

    @Test
    void emptyStores() {
        given(buildingModuleData.getStores()).willReturn(Map.of());

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, buildingModuleData)), "stores", "must not be empty");
    }

    @Test
    void nullInStores() {
        given(buildingModuleData.getStores()).willReturn(CollectionUtils.singleValueMap(StorageType.AMMUNITION, null));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, buildingModuleData)), "stores.%s".formatted(StorageType.AMMUNITION), "must not be null");
    }

    @Test
    void valid() {
        given(buildingModuleData.getStores()).willReturn(Map.of(StorageType.AMMUNITION, 3));

        underTest.validate(Map.of(KEY, buildingModuleData));
    }
}