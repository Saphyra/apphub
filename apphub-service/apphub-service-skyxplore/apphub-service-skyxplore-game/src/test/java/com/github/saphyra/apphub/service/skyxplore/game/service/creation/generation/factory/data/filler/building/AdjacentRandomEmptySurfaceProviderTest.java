package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AdjacentRandomEmptySurfaceProviderTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private AdjacentEmptySurfaceProvider adjacentEmptySurfaceProvider;

    @Mock
    private RandomEmptySurfaceProvider randomEmptySurfaceProvider;

    @InjectMocks
    private AdjacentRandomEmptySurfaceProvider underTest;

    @Mock
    private Surface surface1;

    @Mock
    private Surface surface2;

    @Mock
    private Coordinate coordinate;

    @Mock
    private GameData gameData;

    @Mock
    private Coordinates coordinates;

    @Test
    public void surfaceFound() {
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceId(SURFACE_ID)).willReturn(coordinate);
        given(adjacentEmptySurfaceProvider.getEmptySurfaceNextTo(coordinate, Arrays.asList(surface2), gameData)).willReturn(Optional.of(surface2));
        given(surface1.getSurfaceId()).willReturn(SURFACE_ID);

        Surface result = underTest.getRandomEmptySurfaceNextTo(Arrays.asList(surface1), Arrays.asList(surface2), gameData);

        assertThat(result).isEqualTo(surface2);
    }

    @Test
    public void surfaceNotFound() {
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceId(SURFACE_ID)).willReturn(coordinate);
        given(surface1.getSurfaceId()).willReturn(SURFACE_ID);
        given(randomEmptySurfaceProvider.getEmptyDesertSurface(List.of(surface2), gameData)).willReturn(surface2);

        given(adjacentEmptySurfaceProvider.getEmptySurfaceNextTo(coordinate, Arrays.asList(surface2), gameData)).willReturn(Optional.empty());

        Surface result = underTest.getRandomEmptySurfaceNextTo(Arrays.asList(surface1), Arrays.asList(surface2), gameData);

        assertThat(result).isEqualTo(surface2);
    }
}