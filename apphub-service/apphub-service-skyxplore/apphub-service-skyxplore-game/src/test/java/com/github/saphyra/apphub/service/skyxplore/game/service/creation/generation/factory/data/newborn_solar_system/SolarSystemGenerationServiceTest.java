package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolarSystemGenerationServiceTest {
    private static final Range<Integer> ADDITIONAL_SOLAR_SYSTEMS = new Range<>(32, 434);

    @Mock
    private PlayerAssigner playerAssigner;

    @Mock
    private SolarSystemSeedFactory solarSystemSeedFactory;

    @Mock
    private Random random;

    @Mock
    private SolarSystemPlacerService solarSystemPlacerService;

    @Mock
    private PlanetPlacerService planetPlacerService;

    @InjectMocks
    private SolarSystemGenerationService underTest;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private Player player;

    @Mock
    private Coordinate coordinate;

    @Mock
    private NewbornSolarSystem newbornSolarSystem;

    @Test
    void generateSolarSystems() {
        UUID[] solarSystem = {null};

        List<UUID[]> solarSystems = new ArrayList<>();
        solarSystems.add(solarSystem);

        given(settings.getAdditionalSolarSystems()).willReturn(ADDITIONAL_SOLAR_SYSTEMS);
        given(random.randInt(ADDITIONAL_SOLAR_SYSTEMS)).willReturn(1);
        given(solarSystemSeedFactory.newSolarSystem(settings, false)).willReturn(solarSystem);
        given(solarSystemPlacerService.place(solarSystems)).willReturn(Map.of(coordinate, solarSystem));
        given(planetPlacerService.placePlanets(coordinate, solarSystem)).willReturn(newbornSolarSystem);

        List<NewbornSolarSystem> result = underTest.generateSolarSystems(List.of(player), settings);

        verify(playerAssigner).assignPlayer(player, solarSystems, settings);

        assertThat(result).containsExactly(newbornSolarSystem);
    }
}