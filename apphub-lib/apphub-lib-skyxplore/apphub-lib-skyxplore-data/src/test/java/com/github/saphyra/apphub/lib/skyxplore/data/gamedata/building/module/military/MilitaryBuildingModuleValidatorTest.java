package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.military;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MilitaryBuildingModuleValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingModuleValidator buildingModuleValidator;

    @InjectMocks
    private MilitaryBuildingModuleValidator underTest;

    @Mock
    private MilitaryBuildingModuleData buildingModuleData;

    @AfterEach
    void setUp() {
        then(buildingModuleValidator).should().validate(buildingModuleData);
    }

    @Test
    void valid() {
        underTest.validate(Map.of(KEY, buildingModuleData));
    }
}