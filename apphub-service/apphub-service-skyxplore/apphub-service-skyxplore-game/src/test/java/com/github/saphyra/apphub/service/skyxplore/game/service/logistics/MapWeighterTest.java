package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SurfaceProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MapWeighterTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer WEIGHT = 2;
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private MapWeighter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SurfaceProperties surfaceProperties;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private Coordinates coordinates;

    @Mock
    private Coordinate coordinate;

    @Test
    void getWeightedMap() {
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.getByPlanetId(LOCATION)).willReturn(List.of(surface));
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(gameProperties.getSurface()).willReturn(surfaceProperties);
        given(surfaceProperties.getLogisticsWeight()).willReturn(Map.of(SurfaceType.DESERT, WEIGHT));
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate);

        assertThat(underTest.getWeightedMap(gameData, LOCATION))
            .hasSize(1)
            .containsEntry(coordinate, WEIGHT);
    }
}