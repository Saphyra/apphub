package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SolarSystemProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PlanetPlacerServiceTest {
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final Range<Integer> ORBIT_RADIUS_RANGE = new Range<>(100, 500);
    private static final Integer ORBIT_RADIUS = 200;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private Random random;

    @InjectMocks
    private PlanetPlacerService underTest;

    @Mock
    private SolarSystemProperties solarSystemProperties;

    @Mock
    private Coordinate coordinate;

    @Test
    void placePlanets() {
        given(gameProperties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getPlanetOrbitRadius()).willReturn(ORBIT_RADIUS_RANGE);
        given(random.randInt(ORBIT_RADIUS_RANGE)).willReturn(ORBIT_RADIUS);

        NewbornSolarSystem result = underTest.placePlanets(coordinate, new UUID[]{OWNER_ID, null});

        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getPlanets())
            .containsEntry((double) ORBIT_RADIUS, OWNER_ID)
            .containsEntry(ORBIT_RADIUS * 2d, null);
        assertThat(result.getRadius()).isEqualTo(3 * ORBIT_RADIUS);
    }
}