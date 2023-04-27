package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.planet;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReferredCoordinateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlanetFillerTest {
    private static final Double OUTER_RADIUS = 2d;
    private static final Double INNER_RADIUS = 1d;
    private static final UUID OWNER_ID_1 = UUID.randomUUID();
    private static final UUID OWNER_ID_2 = UUID.randomUUID();
    private static final UUID INNER_PLANET_ID = UUID.randomUUID();
    private static final UUID OUTER_PLANET_ID = UUID.randomUUID();

    @Mock
    private RandomCoordinateProvider randomCoordinateProvider;

    @Mock
    private PlanetFactory planetFactory;

    @Mock
    private ReferredCoordinateFactory referredCoordinateFactory;

    @InjectMocks
    private PlanetFiller underTest;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private GameData gameData;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private Coordinate innerCoordinate;

    @Mock
    private Coordinate outerCoordinate;

    @Mock
    private Planet innerPlanet;

    @Mock
    private Planet outerPlanet;

    @Mock
    private ReferredCoordinate innerReferredCoordinate;

    @Mock
    private ReferredCoordinate outerReferredCoordinate;

    @Mock
    private Planets planets;

    @Mock
    private Coordinates coordinates;

    @Test
    void fillPlanets() {
        Map<Double, UUID> input = Map.of(
            OUTER_RADIUS, OWNER_ID_2,
            INNER_RADIUS, OWNER_ID_1
        );

        given(randomCoordinateProvider.getCoordinateOnCircle(INNER_RADIUS)).willReturn(innerCoordinate);
        given(randomCoordinateProvider.getCoordinateOnCircle(OUTER_RADIUS)).willReturn(outerCoordinate);

        given(planetFactory.create(solarSystem, 0, settings, OWNER_ID_1, INNER_RADIUS)).willReturn(innerPlanet);
        given(planetFactory.create(solarSystem, 1, settings, OWNER_ID_2, OUTER_RADIUS)).willReturn(outerPlanet);

        given(innerPlanet.getPlanetId()).willReturn(INNER_PLANET_ID);
        given(outerPlanet.getPlanetId()).willReturn(OUTER_PLANET_ID);

        given(referredCoordinateFactory.create(INNER_PLANET_ID, innerCoordinate)).willReturn(innerReferredCoordinate);
        given(referredCoordinateFactory.create(OUTER_PLANET_ID, outerCoordinate)).willReturn(outerReferredCoordinate);

        given(gameData.getPlanets()).willReturn(planets);
        given(gameData.getCoordinates()).willReturn(coordinates);

        underTest.fillPlanets(solarSystem, input, gameData, settings);

        verify(planets).put(INNER_PLANET_ID, innerPlanet);
        verify(planets).put(OUTER_PLANET_ID, outerPlanet);

        verify(coordinates).add(innerReferredCoordinate);
        verify(coordinates).add(outerReferredCoordinate);
    }
}