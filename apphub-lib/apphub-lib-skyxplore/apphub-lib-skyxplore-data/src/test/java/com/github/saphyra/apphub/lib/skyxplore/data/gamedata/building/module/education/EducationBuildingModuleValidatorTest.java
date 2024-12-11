package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EducationBuildingModuleValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingModuleValidator buildingModuleValidator;

    @Mock
    private EducationValidator educationValidator;

    @InjectMocks
    private EducationBuildingModuleValidator underTest;

    @Mock
    private Education education;

    @Mock
    private EducationBuildingModuleData buildingModuleData;

    @BeforeEach
    void setUp() {
        given(buildingModuleData.getEducations()).willReturn(List.of(education));
    }

    @AfterEach
    void verify() {
        then(buildingModuleValidator).should().validate(buildingModuleData);
        then(educationValidator).should().validate(List.of(education));
    }

    @Test
    void capacityTooLow() {
        given(buildingModuleData.getCapacity()).willReturn(0);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, buildingModuleData)), "capacity", "too low");
    }

    @Test
    void valid() {
        given(buildingModuleData.getCapacity()).willReturn(1);

        underTest.validate(Map.of(KEY, buildingModuleData));
    }
}