package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AdjacentEmptySurfaceProviderTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private AdjacentEmptySurfaceProvider underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Test
    public void surfaceFound() {
        given(surface.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate2);
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
        given(surface.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate2);
        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(2d);

        Optional<Surface> result = underTest.getEmptySurfaceNextTo(coordinate1, Arrays.asList(surface));

        assertThat(result).isEmpty();
    }
}