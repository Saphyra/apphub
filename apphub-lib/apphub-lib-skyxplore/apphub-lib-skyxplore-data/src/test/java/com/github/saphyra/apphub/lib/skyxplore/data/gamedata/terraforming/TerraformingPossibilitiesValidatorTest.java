package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TerraformingPossibilitiesValidatorTest {
    @Mock
    private ConstructionRequirementsValidator constructionRequirementsValidator;

    @InjectMocks
    private TerraformingPossibilitiesValidator underTest;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Test
    public void emptyPossibilities() {
        Map<SurfaceType, TerraformingPossibilities> map = new HashMap<>();
        map.put(SurfaceType.CONCRETE, new TerraformingPossibilities());

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullInPossibilities() {
        Map<SurfaceType, TerraformingPossibilities> map = new HashMap<>();
        map.put(SurfaceType.CONCRETE, null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullSurfaceType() {
        Map<SurfaceType, TerraformingPossibilities> map = new HashMap<>();
        TerraformingPossibilities terraformingPossibilities = new TerraformingPossibilities();
        terraformingPossibilities.add(terraformingPossibility);
        given(terraformingPossibility.getSurfaceType()).willReturn(null);
        map.put(SurfaceType.CONCRETE, terraformingPossibilities);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void valid() {
        Map<SurfaceType, TerraformingPossibilities> map = new HashMap<>();
        TerraformingPossibilities terraformingPossibilities = new TerraformingPossibilities();
        terraformingPossibilities.add(terraformingPossibility);
        given(terraformingPossibility.getSurfaceType()).willReturn(SurfaceType.DESERT);
        map.put(SurfaceType.CONCRETE, terraformingPossibilities);
        given(terraformingPossibility.getConstructionRequirements()).willReturn(constructionRequirements);

        underTest.validate(map);

        verify(constructionRequirementsValidator).validate(constructionRequirements);
    }
}