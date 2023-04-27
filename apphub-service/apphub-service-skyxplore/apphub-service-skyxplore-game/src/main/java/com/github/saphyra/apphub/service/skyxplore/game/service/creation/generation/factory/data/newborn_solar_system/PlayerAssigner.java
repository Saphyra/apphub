package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlayerAssigner {
    private final SolarSystemSelector solarSystemSelector;
    private final SolarSystemSeedFactory solarSystemSeedFactory;
    private final EmptyPlanetSelector emptyPlanetSelector;

    void assignPlayer(Player player, List<UUID[]> solarSystems, SkyXploreGameSettings settings) {
        Optional<UUID[]> maybeSolarSystem = solarSystemSelector.selectSolarSystem(solarSystems, settings);

        UUID[] solarSystem;
        if (maybeSolarSystem.isPresent()) {
            solarSystem = maybeSolarSystem.get();
        } else {
            solarSystem = solarSystemSeedFactory.newSolarSystem(settings, true);
            solarSystems.add(solarSystem);
        }

        int emptyPlanetIndex = emptyPlanetSelector.selectEmptyPlanet(solarSystem);
        solarSystem[emptyPlanetIndex] = player.getUserId();
    }
}
