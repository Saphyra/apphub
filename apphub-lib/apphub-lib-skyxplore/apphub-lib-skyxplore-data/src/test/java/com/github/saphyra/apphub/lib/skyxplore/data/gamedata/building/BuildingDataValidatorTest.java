package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BuildingDataValidatorTest {
    private static final String BUILDING_TYPE = "building-type";

    @Mock
    private ConstructionRequirementsValidator constructionRequirementsValidator;

    @Mock
    private GameDataItemValidator gameDataItemValidator;

    @InjectMocks
    private BuildingDataValidator underTest;

    @Mock
    private BuildingData buildingData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @AfterEach
    public void validate() {
        verify(gameDataItemValidator).validate(buildingData);
    }

    @Test
    public void nullBuildingType() {
        given(buildingData.getBuildingType()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(buildingData))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void emptyConstructionRequirements() {
        given(buildingData.getBuildingType()).willReturn(BUILDING_TYPE);
        given(buildingData.getConstructionRequirements()).willReturn(Collections.emptyMap());

        assertThat(catchThrowable(() -> underTest.validate(buildingData))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void valid() {
        given(buildingData.getBuildingType()).willReturn(BUILDING_TYPE);
        Map<Integer, ConstructionRequirements> constructionRequirementsMap = new HashMap<>();
        constructionRequirementsMap.put(1, constructionRequirements);
        given(buildingData.getConstructionRequirements()).willReturn(constructionRequirementsMap);

        underTest.validate(buildingData);
    }
}