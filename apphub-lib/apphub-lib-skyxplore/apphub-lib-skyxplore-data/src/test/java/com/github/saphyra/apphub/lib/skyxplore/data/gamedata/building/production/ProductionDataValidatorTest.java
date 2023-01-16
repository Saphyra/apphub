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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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

    @Test
    public void nullMap() {
        assertThat(catchThrowable(() -> underTest.validate(null))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void emptyMap() {
        assertThat(catchThrowable(() -> underTest.validate(Collections.emptyMap()))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullData() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullPlaced() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void emptyPlaced() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Collections.emptyList());

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void placedContainsNull() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Arrays.asList((SurfaceType) null));

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullRequiredSkill() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        given(productionData.getRequiredSkill()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullConstructionRequirements() {
        Map<String, ProductionData> map = new HashMap<>();
        map.put(KEY, productionData);
        given(productionData.getPlaced()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        given(productionData.getRequiredSkill()).willReturn(SkillType.AIMING);
        given(productionData.getConstructionRequirements()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
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