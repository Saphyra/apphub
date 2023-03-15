package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.home_planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ClosestSystemFinderTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private FreePlanetCounter freePlanetCounter;

    @InjectMocks
    private ClosestSystemFinder underTest;

    @Mock
    private SolarSystem anchorSystem;

    @Mock
    private SolarSystem solarSystemWithoutEmptyPlanet;

    @Mock
    private SolarSystem closestSolarSystem;

    @Mock
    private SolarSystem farSolarSystem;

    @Mock
    private Coordinate anchorCoordinate;

    @Mock
    private Coordinate closestCoordinate;

    @Mock
    private Coordinate farCoordinate;

    @Mock
    private Coordinate withoutEmptyPlanetCoordinate;

    @Mock
    private CoordinateModel anchorModel;

    @Test
    public void getClosestSystemWithEmptyPlanet() {
        Map<Coordinate, SolarSystem> solarSystemMap = CollectionUtils.toMap(
            new BiWrapper<>(anchorCoordinate, anchorSystem),
            new BiWrapper<>(withoutEmptyPlanetCoordinate, solarSystemWithoutEmptyPlanet),
            new BiWrapper<>(farCoordinate, farSolarSystem),
            new BiWrapper<>(closestCoordinate, closestSolarSystem)
        );

        given(freePlanetCounter.getNumberOfFreePlanets(solarSystemWithoutEmptyPlanet)).willReturn(0L);
        given(freePlanetCounter.getNumberOfFreePlanets(farSolarSystem)).willReturn(1L);
        given(freePlanetCounter.getNumberOfFreePlanets(closestSolarSystem)).willReturn(1L);

        given(anchorSystem.getCoordinate()).willReturn(anchorModel);
        given(anchorModel.getCoordinate()).willReturn(anchorCoordinate);

        given(distanceCalculator.getDistance(farCoordinate, anchorCoordinate)).willReturn(10d);
        given(distanceCalculator.getDistance(closestCoordinate, anchorCoordinate)).willReturn(5d);

        SolarSystem result = underTest.getClosestSystemWithEmptyPlanet(anchorSystem, solarSystemMap);

        assertThat(result).isEqualTo(closestSolarSystem);
    }
}