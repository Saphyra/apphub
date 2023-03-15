package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UniverseSizeCalculatorTest {
    private static final int X = 345;
    private static final int Y = 346;

    @InjectMocks
    private UniverseSizeCalculator underTest;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void calculate() {
        given(solarSystem.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(new Coordinate(X, Y));

        int result = underTest.calculateUniverseSize(Arrays.asList(solarSystem));

        assertThat(result).isEqualTo(Y);
    }
}