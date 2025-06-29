package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TerraformingPossibilitiesTest {
    private final TerraformingPossibilities underTest = new TerraformingPossibilities();

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @Test
    void findBySurfaceTypeValidated() {
        underTest.add(terraformingPossibility);
        given(terraformingPossibility.getSurfaceType()).willReturn(SurfaceType.DESERT);

        assertThat(underTest.findBySurfaceTypeValidated(SurfaceType.DESERT)).isEqualTo(terraformingPossibility);
    }

    @Test
    void findBySurfaceTypeValidated_notFound() {
        assertThat(catchThrowable(() -> underTest.findBySurfaceTypeValidated(SurfaceType.DESERT))).isInstanceOf(IllegalArgumentException.class);
    }
}