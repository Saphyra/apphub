package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SuitableSolarSystemFinderTest {
    private final SuitableSolarSystemFinder underTest = new SuitableSolarSystemFinder();

    @Mock
    private SkyXploreGameSettings settings;

    @Test
    void getSuitableSystems() {
        UUID[] emptySystem = {};
        UUID[] fullSystem = {UUID.randomUUID(), null};
        UUID[] suitableSystem = {null};
        List<UUID[]> solarSystems = new ArrayList<>();
        solarSystems.add(emptySystem);
        solarSystems.add(fullSystem);
        solarSystems.add(suitableSystem);

        given(settings.getMaxPlayersPerSolarSystem()).willReturn(1);

        List<UUID[]> result = underTest.getSuitableSolarSystems(solarSystems, settings);

        assertThat(result).containsExactly(suitableSystem);
    }
}