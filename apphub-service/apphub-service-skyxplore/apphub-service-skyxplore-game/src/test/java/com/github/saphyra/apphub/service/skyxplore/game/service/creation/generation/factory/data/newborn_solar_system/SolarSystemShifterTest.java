package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SolarSystemProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SolarSystemShifterTest {
    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private SolarSystemShifter underTest;

    @Mock
    private SolarSystemProperties solarSystemProperties;

    @Test
    void shiftSolarSystems() {
        UUID[] solarSystem1 = {};
        UUID[] solarSystem2 = {null};
        Map<Coordinate, UUID[]> solarSystems = Map.of(
            new Coordinate(50, 50), solarSystem1,
            new Coordinate(-10, 0), solarSystem2
        );

        given(gameProperties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getPadding()).willReturn(10);

        Map<Coordinate, UUID[]> result = underTest.shiftSolarSystems(solarSystems);

        assertThat(result).containsEntry(new Coordinate(70, 60), solarSystem1);
        assertThat(result).containsEntry(new Coordinate(10, 10), solarSystem2);
    }
}