package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
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
class ConstructionAreaDataValidatorTest {
    private static final String KEY = "key";

    @Mock
    private GameDataItemValidator gameDataItemValidator;

    @Mock
    private ConstructionRequirementsValidator constructionRequirementsValidator;

    @InjectMocks
    private ConstructionAreaDataValidator underTest;

    @Mock
    private ConstructionAreaData constructionAreaData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @BeforeEach
    void setUp() {
        given(constructionAreaData.getConstructionRequirements()).willReturn(constructionRequirements);
    }

    @AfterEach
    void verify() {
        then(gameDataItemValidator).should().validate(constructionAreaData);
        then(constructionRequirementsValidator).should().validate(constructionRequirements);
    }

    @Test
    void emptySupportedSurfaces() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, constructionAreaData)), "supportedSurfaces", "must not be empty");
    }

    @Test
    void emptySlots() {
        given(constructionAreaData.getSupportedSurfaces()).willReturn(List.of(SurfaceType.CONCRETE));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(Map.of(KEY, constructionAreaData)), "slots", "must not be empty");
    }

    @Test
    void valid() {
        given(constructionAreaData.getSupportedSurfaces()).willReturn(List.of(SurfaceType.CONCRETE));
        given(constructionAreaData.getSlots()).willReturn(Map.of(BuildingModuleCategory.DOCK, 3));

        underTest.validate(Map.of(KEY, constructionAreaData));
    }
}