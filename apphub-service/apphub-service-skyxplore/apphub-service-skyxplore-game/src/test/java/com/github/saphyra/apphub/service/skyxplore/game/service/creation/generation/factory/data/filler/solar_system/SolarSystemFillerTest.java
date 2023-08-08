package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystems;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinateFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.planet.PlanetFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.NewbornSolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolarSystemFillerTest {
    private static final Integer RADIUS = 2345;
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private SolarSystemFactory solarSystemFactory;

    @Mock
    private PlanetFiller planetFiller;

    @Mock
    private ReferredCoordinateFactory referredCoordinateFactory;

    @InjectMocks
    private SolarSystemFiller underTest;

    @Mock
    private NewbornSolarSystem newbornSolarSystem;

    @Mock
    private GameData gameData;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private SolarSystems solarSystems;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Coordinate coordinate;

    @Mock
    private ReferredCoordinate referredCoordinate;

    @Mock
    private Coordinates coordinates;

    @Test
    void fillNewbornSolarSystems() {
        Map<Double, UUID> planets = Map.of(23d, UUID.randomUUID());

        given(newbornSolarSystem.getRadius()).willReturn(RADIUS);
        given(gameData.getSolarSystems()).willReturn(solarSystems);
        given(solarSystemFactory.create(RADIUS, solarSystems)).willReturn(solarSystem);
        given(solarSystem.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(newbornSolarSystem.getCoordinate()).willReturn(coordinate);
        given(referredCoordinateFactory.create(SOLAR_SYSTEM_ID, coordinate)).willReturn(referredCoordinate);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(newbornSolarSystem.getPlanets()).willReturn(planets);


        underTest.fillNewbornSolarSystems(List.of(newbornSolarSystem), gameData, settings);

        verify(solarSystems).add(solarSystem);
        verify(coordinates).add(referredCoordinate);
        verify(planetFiller).fillPlanets(solarSystem, planets, gameData, settings);
    }
}