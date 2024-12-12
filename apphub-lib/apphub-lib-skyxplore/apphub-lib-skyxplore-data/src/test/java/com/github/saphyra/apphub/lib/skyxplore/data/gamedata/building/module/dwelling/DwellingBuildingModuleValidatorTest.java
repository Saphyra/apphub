package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling;

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
class DwellingBuildingModuleValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingModuleValidator buildingModuleValidator;

    @InjectMocks
    private DwellingBuildingModuleValidator underTest;

    @Mock
    private DwellingBuildingModuleData buildingModuleData;

    @AfterEach
    void verify() {
        then(buildingModuleValidator).should().validate(buildingModuleData);
    }

    @Test
    void capacityTooLow() {
        given(buildingModuleData.getCapacity()).willReturn(0);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, buildingModuleData)), "capacity", "too low");
    }

    @Test
    void moraleRecoveryTooLow() {
        given(buildingModuleData.getCapacity()).willReturn(1);
        given(buildingModuleData.getMoraleRecovery()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, buildingModuleData)), "moraleRecovery", "too low");
    }

    @Test
    void valid() {
        given(buildingModuleData.getCapacity()).willReturn(1);
        given(buildingModuleData.getMoraleRecovery()).willReturn(0);

        underTest.validate(Map.of(KEY, buildingModuleData));
    }
}