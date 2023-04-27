package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SolarSystemSelectorTest {
    private static final UUID[] SOLAR_SYSTEM = {null};
    private static final List<UUID[]> SOLAR_SYSTEMS = new ArrayList<>() {{
        add(SOLAR_SYSTEM);
    }};

    @Mock
    private SuitableSolarSystemFinder suitableSolarSystemFinder;

    @Mock
    private Random random;

    @InjectMocks
    private SolarSystemSelector underTest;

    @Mock
    private SkyXploreGameSettings settings;

    @Test
    void selectSolarSystem_selectExisting() {
        given(suitableSolarSystemFinder.getSuitableSolarSystems(SOLAR_SYSTEMS, settings)).willReturn(SOLAR_SYSTEMS);
        given(random.randInt(0, 1)).willReturn(0);

        Optional<UUID[]> result = underTest.selectSolarSystem(SOLAR_SYSTEMS, settings);

        assertThat(result).contains(SOLAR_SYSTEM);
    }

    @Test
    void selectSolarSystem_selectNew() {
        given(suitableSolarSystemFinder.getSuitableSolarSystems(SOLAR_SYSTEMS, settings)).willReturn(SOLAR_SYSTEMS);
        given(random.randInt(0, 1)).willReturn(1);

        Optional<UUID[]> result = underTest.selectSolarSystem(SOLAR_SYSTEMS, settings);

        assertThat(result).isEmpty();
    }
}