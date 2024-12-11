package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BuildingModuleValidatorTest {
    @Mock
    private GameDataItemValidator gameDataItemValidator;

    @Mock
    private ConstructionRequirementsValidator constructionRequirementsValidator;

    @InjectMocks
    private BuildingModuleValidator underTest;

    @Mock
    private BuildingModuleData buildingModuleData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @BeforeEach
    void setUp() {
        given(buildingModuleData.getConstructionRequirements()).willReturn(constructionRequirements);
    }

    @AfterEach
    void verify() {
        then(gameDataItemValidator).should().validate(buildingModuleData);
        then(constructionRequirementsValidator).should().validate(constructionRequirements);
    }

    @Test
    void nullCategory() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(buildingModuleData), "category", "must not be null");
    }

    @Test
    void valid() {
        given(buildingModuleData.getCategory()).willReturn(BuildingModuleCategory.DOCK);

        underTest.validate(buildingModuleData);
    }
}