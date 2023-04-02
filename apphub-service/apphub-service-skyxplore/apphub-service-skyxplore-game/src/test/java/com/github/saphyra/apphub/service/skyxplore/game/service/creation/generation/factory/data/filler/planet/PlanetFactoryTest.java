package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.planet;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.PlanetProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PlanetFactoryTest {
    private static final double ORBIT_RADIUS = 34d;
    private static final int PLANET_INDEX = 3;
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final Range<Integer> PLANET_SIZE_RANGE = new Range<>(0, 4);
    private static final Integer PLANET_SIZE = 34;
    private static final Range<Double> ORBIT_SPEED_RANGE = new Range<>(23d, 23d);
    private static final Double ORBIT_SPEED = 234d;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private Random random;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private PlanetFactory underTest;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private PlanetProperties planetProperties;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PLANET_ID);
        given(solarSystem.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(solarSystem.getDefaultName()).willReturn(DEFAULT_NAME);
        given(gameProperties.getPlanet()).willReturn(planetProperties);
        given(settings.getPlanetSize()).willReturn(PLANET_SIZE_RANGE);
        given(random.randInt(PLANET_SIZE_RANGE)).willReturn(PLANET_SIZE);
        given(planetProperties.getOrbitSpeed()).willReturn(ORBIT_SPEED_RANGE);
        given(random.randDouble(ORBIT_SPEED_RANGE)).willReturn(ORBIT_SPEED);

        Planet result = underTest.create(solarSystem, PLANET_INDEX, settings, OWNER_ID, ORBIT_RADIUS);

        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME + " D");
        assertThat(result.getSize()).isEqualTo(PLANET_SIZE);
        assertThat(result.getOrbitRadius()).isEqualTo(ORBIT_RADIUS);
        assertThat(result.getOrbitSpeed()).isEqualTo(ORBIT_SPEED);
        assertThat(result.getOwner()).isEqualTo(OWNER_ID);
    }
}