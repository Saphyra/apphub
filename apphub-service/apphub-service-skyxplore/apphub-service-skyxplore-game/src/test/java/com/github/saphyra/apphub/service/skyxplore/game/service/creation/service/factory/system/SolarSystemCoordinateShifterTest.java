package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SolarSystemProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemCoordinateShifterTest {
    private static final Double MIN_X = 123d;
    private static final Double X = MIN_X + 1;
    private static final Double MIN_Y = 354d;
    private static final Double Y = MIN_Y + 1;
    private static final Integer MIN_DISTANCE = 2314;
    private static final Integer MAX_DISTANCE = 523412;

    @Mock
    private GameProperties gameCreationProperties;

    @InjectMocks
    private SolarSystemCoordinateShifter underTest;

    @Mock
    private SolarSystem solarSystem1;

    @Mock
    private SolarSystem solarSystem2;

    @Mock
    private CoordinateModel coordinateModel1;

    @Mock
    private CoordinateModel coordinateModel2;

    @Mock
    private SolarSystemProperties solarSystemProperties;

    @Test
    public void shiftCoordinates() {

        given(solarSystem1.getCoordinate()).willReturn(coordinateModel1);
        given(solarSystem2.getCoordinate()).willReturn(coordinateModel2);
        given(coordinateModel1.getX()).willReturn(MIN_X);
        given(coordinateModel2.getX()).willReturn(X);
        given(coordinateModel1.getY()).willReturn(Y);
        given(coordinateModel2.getY()).willReturn(MIN_Y);

        given(gameCreationProperties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getSolarSystemDistance()).willReturn(new Range<>(MIN_DISTANCE, MAX_DISTANCE));

        underTest.shiftCoordinates(Arrays.asList(solarSystem1, solarSystem2));

        double diffX = -1 * MIN_X + MIN_DISTANCE;
        double diffY = -1 * MIN_Y + MIN_DISTANCE;

        ArgumentCaptor<Coordinate> argumentCaptor1 = ArgumentCaptor.forClass(Coordinate.class);
        verify(coordinateModel1).setCoordinate(argumentCaptor1.capture());
        assertThat(argumentCaptor1.getValue().getX()).isEqualTo(MIN_X + diffX);
        assertThat(argumentCaptor1.getValue().getY()).isEqualTo(Y + diffY);

        ArgumentCaptor<Coordinate> argumentCaptor2 = ArgumentCaptor.forClass(Coordinate.class);
        verify(coordinateModel2).setCoordinate(argumentCaptor2.capture());
        assertThat(argumentCaptor2.getValue().getX()).isEqualTo(X + diffX);
        assertThat(argumentCaptor2.getValue().getY()).isEqualTo(MIN_Y + diffY);
    }
}