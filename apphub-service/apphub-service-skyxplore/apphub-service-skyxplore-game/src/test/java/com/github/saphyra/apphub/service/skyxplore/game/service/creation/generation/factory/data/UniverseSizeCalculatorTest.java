package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SolarSystemProperties;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.NewbornSolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UniverseSizeCalculatorTest {
    private static final Integer PADDING = 234;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private UniverseSizeCalculator underTest;

    @Mock
    private SolarSystemProperties solarSystemProperties;

    @Mock
    private NewbornSolarSystem newbornSolarSystem1;

    @Mock
    private NewbornSolarSystem newbornSolarSystem2;

    @Test
    void calculate() {
        given(gameProperties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getPadding()).willReturn(PADDING);

        given(newbornSolarSystem1.getCoordinate()).willReturn(new Coordinate(2, 3));
        given(newbornSolarSystem2.getCoordinate()).willReturn(new Coordinate(2, 5));

        int universeSize = underTest.calculate(List.of(newbornSolarSystem1, newbornSolarSystem2));

        assertThat(universeSize).isEqualTo(5 + PADDING);
    }
}