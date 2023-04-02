package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SuitableSolarSystemFinder {
    List<UUID[]> getSuitableSolarSystems(List<UUID[]> solarSystems, SkyXploreGameSettings settings) {
        return solarSystems.stream()
            .filter(solarSystem -> hasSuitablePlanet(solarSystem, settings))
            .toList();
    }

    private boolean hasSuitablePlanet(UUID[] planet, SkyXploreGameSettings settings) {
        int maxNumberOfOccupiedPlanets = Math.min(planet.length, settings.getMaxPlayersPerSolarSystem());

        int occupiedPlanetsCount = (int) Arrays.stream(planet)
            .filter(userId -> !isNull(userId))
            .count();

        return occupiedPlanetsCount < maxNumberOfOccupiedPlanets;
    }
}
