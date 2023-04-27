package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building.MatchingSurfaceTypeFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MatchingSurfaceTypeFilterTest {
    @InjectMocks
    private MatchingSurfaceTypeFilter underTest;

    @Mock
    private Surface surface1;

    @Mock
    private Surface surface2;

    @Test
    public void getSurfacesWithMatchingType() {
        given(surface1.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(surface2.getSurfaceType()).willReturn(SurfaceType.DESERT);

        List<Surface> result = underTest.getSurfacesWithMatchingType(Arrays.asList(surface1, surface2), SurfaceType.CONCRETE);

        assertThat(result).containsExactly(surface1);
    }
}