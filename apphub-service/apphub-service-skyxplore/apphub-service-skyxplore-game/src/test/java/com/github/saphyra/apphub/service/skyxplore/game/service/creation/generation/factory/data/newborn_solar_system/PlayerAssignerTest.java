package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
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
class PlayerAssignerTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SolarSystemSelector solarSystemSelector;

    @Mock
    private SolarSystemSeedFactory solarSystemSeedFactory;

    @Mock
    private EmptyPlanetSelector emptyPlanetSelector;

    @InjectMocks
    private PlayerAssigner underTest;

    @Mock
    private Player player;

    @Mock
    private SkyXploreGameSettings settings;

    @Test
    void assignPlayer_emptySolarSystemFound() {
        UUID[] solarSystem = {null};
        List<UUID[]> solarSystems = new ArrayList<>();
        solarSystems.add(solarSystem);

        given(solarSystemSelector.selectSolarSystem(solarSystems, settings)).willReturn(Optional.of(solarSystem));
        given(emptyPlanetSelector.selectEmptyPlanet(solarSystem)).willReturn(0);
        given(player.getUserId()).willReturn(USER_ID);

        underTest.assignPlayer(player, solarSystems, settings);

        assertThat(solarSystem).containsExactly(USER_ID);
    }

    @Test
    void assignPlayer_noSuitableSolarSystem() {
        UUID[] solarSystem = {};
        UUID[] newSolarSystem = {null};
        List<UUID[]> solarSystems = new ArrayList<>();
        solarSystems.add(solarSystem);

        given(solarSystemSelector.selectSolarSystem(solarSystems, settings)).willReturn(Optional.empty());
        given(solarSystemSeedFactory.newSolarSystem(settings, true)).willReturn(newSolarSystem);

        given(emptyPlanetSelector.selectEmptyPlanet(newSolarSystem)).willReturn(0);
        given(player.getUserId()).willReturn(USER_ID);

        underTest.assignPlayer(player, solarSystems, settings);

        assertThat(solarSystems).hasSize(2);
        assertThat(solarSystems.get(0)).isEmpty();
        assertThat(solarSystems.get(1)).containsExactly(USER_ID);
    }
}