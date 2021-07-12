package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
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
public class AdjacentRandomEmptySurfaceProviderTest {
    @Mock
    private AdjacentEmptySurfaceProvider adjacentEmptySurfaceProvider;

    @InjectMocks
    private AdjacentRandomEmptySurfaceProvider underTest;

    @Mock
    private Surface surface1;

    @Mock
    private Surface surface2;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Test
    public void surfaceFound() {
        given(surface1.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);
        given(adjacentEmptySurfaceProvider.getEmptySurfaceNextTo(coordinate, Arrays.asList(surface2))).willReturn(Optional.of(surface2));

        Surface result = underTest.getRandomEmptySurfaceNextTo(Arrays.asList(surface1), Arrays.asList(surface2));

        assertThat(result).isEqualTo(surface2);
    }

    @Test(expected = IllegalStateException.class)
    public void surfaceNotFound() {
        given(surface1.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        given(adjacentEmptySurfaceProvider.getEmptySurfaceNextTo(coordinate, Arrays.asList(surface2))).willReturn(Optional.empty());

        underTest.getRandomEmptySurfaceNextTo(Arrays.asList(surface1), Arrays.asList(surface2));
    }
}