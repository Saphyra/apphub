package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductionDataValidatorTest {
    private static final String KEY = "key";

    @Mock
    private ConstructionRequirementsValidator constructionRequirementsValidator;

    @InjectMocks
    private ProductionDataValidator underTest;

    @Mock
    private ProductionData productionData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Test(expected = NullPointerException.class)
    public void nullMap() {
        underTest.validate(null);
    }

    @Test(expected = IllegalStateException.class)
    public void emptyMap() {
        underTest.validate(Collections.emptyMap());
    }

    @Test(expected = IllegalStateException.class)
    public void nullData() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, null);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void nullPlaced() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(null);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void emptyPlaced() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Collections.emptyList());

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void placedContainsNull() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Arrays.asList((SurfaceType) null));

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void nullRequiredSkill() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        given(productionData.getRequiredSkill()).willReturn(null);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void nullConstructionRequirements() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        given(productionData.getRequiredSkill()).willReturn(SkillType.AIMING);
        given(productionData.getConstructionRequirements()).willReturn(null);

        underTest.validate(map);
    }

    @Test
    public void valid() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        given(productionData.getRequiredSkill()).willReturn(SkillType.AIMING);
        given(productionData.getConstructionRequirements()).willReturn(constructionRequirements);

        underTest.validate(map);

        verify(constructionRequirementsValidator).validate(constructionRequirements);
    }
}