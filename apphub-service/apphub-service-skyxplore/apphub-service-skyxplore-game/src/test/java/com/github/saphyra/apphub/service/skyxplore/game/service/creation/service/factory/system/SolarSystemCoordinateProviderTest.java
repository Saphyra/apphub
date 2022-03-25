package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SolarSystemProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemCoordinateProviderTest {
    private static final Integer MIN_SOLAR_SYSTEM_DISTANCE = 10;
    private static final Integer MAX_SOLAR_SYSTEM_DISTANCE = 435;
    private static final Double DISTANCE = 11d;

    @Mock
    private GameProperties gameCreationProperties;

    @Mock
    private Random random;

    @Mock
    private RandomCoordinateProvider randomCoordinateProvider;

    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private SolarSystemCoordinateProvider underTest;

    @Mock
    private SolarSystem anchorSolarSystem;

    @Mock
    private SolarSystemProperties solarSystemProperties;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate anchorCoordinate;

    @Mock
    private Coordinate generatedCoordinate;

    @Mock
    private Coordinate modifiedCoordinate;

    @Test
    public void getCoordinates() {
        given(gameCreationProperties.getSolarSystem()).willReturn(solarSystemProperties);

        given(anchorSolarSystem.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(anchorCoordinate);

        given(solarSystemProperties.getSolarSystemDistance()).willReturn(new Range<>(MIN_SOLAR_SYSTEM_DISTANCE, MAX_SOLAR_SYSTEM_DISTANCE));
        given(random.randInt(0, 0)).willReturn(0);

        given(randomCoordinateProvider.getCoordinateInCircle(MAX_SOLAR_SYSTEM_DISTANCE)).willReturn(generatedCoordinate);
        given(distanceCalculator.getDistance(generatedCoordinate, GameConstants.ORIGO)).willReturn(DISTANCE);

        given(generatedCoordinate.add(anchorCoordinate)).willReturn(modifiedCoordinate);

        given(distanceCalculator.getDistance(modifiedCoordinate, anchorCoordinate)).willReturn(DISTANCE);

        Coordinate result = underTest.getCoordinate(Arrays.asList(anchorSolarSystem));

        assertThat(result).isEqualTo(modifiedCoordinate);
    }
}