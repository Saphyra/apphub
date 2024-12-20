package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dock;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ShipSize;
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
class DockBuildingModuleValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingModuleValidator buildingModuleValidator;

    @InjectMocks
    private DockBuildingModuleValidator underTest;

    @Mock
    private DockBuildingModuleData buildingModuleData;

    @AfterEach
    void verify() {
        then(buildingModuleValidator).should().validate(buildingModuleData);
    }

    @Test
    void nullSize() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, buildingModuleData)), "size", "must not be null");
    }

    @Test
    void valid() {
        given(buildingModuleData.getSize()).willReturn(ShipSize.DRONE);

        underTest.validate(Map.of(KEY, buildingModuleData));
    }
}