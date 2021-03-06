package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AdjacentEmptySurfaceProviderTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private AdjacentEmptySurfaceProvider underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Test
    public void surfaceFound() {
        given(surface.getCoordinate()).willReturn(coordinate2);
        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(1d);

        Optional<Surface> result = underTest.getEmptySurfaceNextTo(coordinate1, Arrays.asList(surface));

        assertThat(result).contains(surface);
    }

    @Test
    public void hasBuilding() {
        given(surface.getBuilding()).willReturn(building);

        Optional<Surface> result = underTest.getEmptySurfaceNextTo(coordinate1, Arrays.asList(surface));

        assertThat(result).isEmpty();
    }

    @Test
    public void surfaceNotFound() {
        given(surface.getCoordinate()).willReturn(coordinate2);
        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(2d);

        Optional<Surface> result = underTest.getEmptySurfaceNextTo(coordinate1, Arrays.asList(surface));

        assertThat(result).isEmpty();
    }
}