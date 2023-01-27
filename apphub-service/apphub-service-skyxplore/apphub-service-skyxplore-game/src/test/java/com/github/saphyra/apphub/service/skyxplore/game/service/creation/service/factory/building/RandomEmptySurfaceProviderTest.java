package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class RandomEmptySurfaceProviderTest {
    @InjectMocks
    private RandomEmptySurfaceProvider underTest;

    @Mock
    public Surface surface1;

    @Mock
    public Surface surface2;

    @Mock
    public Surface surface3;

    @Mock
    private Building building;

    @Test
    public void getRandomEmptySurface() {
        given(surface1.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(surface2.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(surface3.getSurfaceType()).willReturn(SurfaceType.DESERT);

        given(surface2.getBuilding()).willReturn(building);

        Surface result = underTest.getRandomEmptySurface(Arrays.asList(surface1, surface2, surface3));

        assertThat(result).isEqualTo(surface3);
    }

    @Test
    public void surfaceNotFound() {
        assertThat(catchThrowable(() -> underTest.getRandomEmptySurface(Collections.emptyList()))).isInstanceOf(IllegalStateException.class);
    }
}