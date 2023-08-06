package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SurfaceFillerTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer PLANET_SIZE = 34;
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private SurfaceFactory surfaceFactory;

    @Mock
    private SurfaceMapFactory surfaceMapFactory;

    @Mock
    private ReferredCoordinateFactory referredCoordinateFactory;

    @InjectMocks
    private SurfaceFiller underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private ReferredCoordinate referredCoordinate;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Coordinates coordinates;

    @Test
    void fillSurfaces() {
        SurfaceType[][] surfaceMap = new SurfaceType[1][1];
        surfaceMap[0] = new SurfaceType[]{SurfaceType.CONCRETE};

        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(planet.getSize()).willReturn(PLANET_SIZE);
        given(surfaceMapFactory.createSurfaceMap(PLANET_SIZE)).willReturn(surfaceMap);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(surfaceFactory.create(PLANET_ID, SurfaceType.CONCRETE)).willReturn(surface);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(referredCoordinateFactory.create(SURFACE_ID, new Coordinate(0, 0))).willReturn(referredCoordinate);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(gameData.getCoordinates()).willReturn(coordinates);

        underTest.fillSurfaces(gameData);

        verify(surfaces).add(surface);
        verify(coordinates).add(referredCoordinate);
    }
}