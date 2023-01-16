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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmptySurfaceProviderTest {
    @Mock
    private MatchingSurfaceTypeFilter matchingSurfaceTypeFilter;

    @Mock
    private RandomEmptySurfaceProvider randomEmptySurfaceProvider;

    @Mock
    private AdjacentRandomEmptySurfaceProvider adjacentRandomEmptySurfaceProvider;

    @InjectMocks
    private EmptySurfaceProvider underTest;

    @Mock
    private Surface surface;

    @Mock
    private Surface surfaceWithBuilding;

    @Mock
    private Building building;

    @Test
    public void randomEmptySurface() {
        given(matchingSurfaceTypeFilter.getSurfacesWithMatchingType(Arrays.asList(surface), SurfaceType.CONCRETE)).willReturn(Collections.emptyList());
        given(randomEmptySurfaceProvider.getRandomEmptySurface(Arrays.asList(surface))).willReturn(surface);

        Surface result = underTest.getEmptySurfaceForType(SurfaceType.CONCRETE, Arrays.asList(surface));

        assertThat(result).isEqualTo(surface);
        verify(surface).setSurfaceType(SurfaceType.CONCRETE);
    }

    @Test
    public void emptySurfaceWithMatchingType() {
        given(matchingSurfaceTypeFilter.getSurfacesWithMatchingType(Arrays.asList(surfaceWithBuilding, surface), SurfaceType.CONCRETE)).willReturn(Arrays.asList(surfaceWithBuilding, surface));
        given(surface.getBuilding()).willReturn(null);
        given(surfaceWithBuilding.getBuilding()).willReturn(building);

        Surface result = underTest.getEmptySurfaceForType(SurfaceType.CONCRETE, Arrays.asList(surfaceWithBuilding, surface));

        assertThat(result).isEqualTo(surface);
    }

    @Test
    public void randomEmptySurfaceNextToType() {
        given(matchingSurfaceTypeFilter.getSurfacesWithMatchingType(Arrays.asList(surfaceWithBuilding, surface), SurfaceType.CONCRETE)).willReturn(Arrays.asList(surfaceWithBuilding));
        given(surfaceWithBuilding.getBuilding()).willReturn(building);

        given(adjacentRandomEmptySurfaceProvider.getRandomEmptySurfaceNextTo(Arrays.asList(surfaceWithBuilding), Arrays.asList(surfaceWithBuilding, surface))).willReturn(surface);

        Surface result = underTest.getEmptySurfaceForType(SurfaceType.CONCRETE, Arrays.asList(surfaceWithBuilding, surface));

        assertThat(result).isEqualTo(surface);
        verify(surface).setSurfaceType(SurfaceType.CONCRETE);
    }
}