package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.solar_system;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SolarSystemFactoryTest {
    private static final int RADIUS = 324;
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_SOLAR_SYSTEM_NAME = "default-solar-system-name";
    private static final String SOLAR_SYSTEM_NAME = "solar-system-name";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SolarSystemNames solarSystemNames;

    @InjectMocks
    private SolarSystemFactory underTest;

    @Mock
    private SolarSystem solarSystem;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(SOLAR_SYSTEM_ID);
        given(solarSystem.getDefaultName()).willReturn(DEFAULT_SOLAR_SYSTEM_NAME);
        given(solarSystemNames.getRandomStarName(List.of(DEFAULT_SOLAR_SYSTEM_NAME))).willReturn(SOLAR_SYSTEM_NAME);

        SolarSystem result = underTest.create(RADIUS, List.of(solarSystem));

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(SOLAR_SYSTEM_NAME);
    }
}