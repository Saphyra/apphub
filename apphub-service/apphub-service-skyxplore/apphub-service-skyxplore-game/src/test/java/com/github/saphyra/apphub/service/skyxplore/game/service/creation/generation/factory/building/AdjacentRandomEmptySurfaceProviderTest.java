package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building.AdjacentEmptySurfaceProvider;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building.AdjacentRandomEmptySurfaceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void surfaceNotFound() {
        given(surface1.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        given(adjacentEmptySurfaceProvider.getEmptySurfaceNextTo(coordinate, Arrays.asList(surface2))).willReturn(Optional.empty());

        assertThat(catchThrowable(() -> underTest.getRandomEmptySurfaceNextTo(Arrays.asList(surface1), Arrays.asList(surface2)))).isInstanceOf(IllegalStateException.class);
    }
}