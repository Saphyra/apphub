package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class RandomEmptyPlanetFinder {
    private final Random random;

    Planet randomEmptyPlanet(Universe universe) {
        List<Planet> emptyPlanets = universe.getSystems()
            .values()
            .stream()
            .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
            .filter(planet -> isNull(planet.getOwner()))
            .collect(Collectors.toList());

        return randomEmptyPlanet(emptyPlanets);
    }

    Planet randomEmptyPlanet(SolarSystem allianceHomeSystem) {
        List<Planet> emptyPlanets = allianceHomeSystem.getPlanets()
            .values()
            .stream()
            .filter(planet -> isNull(planet.getOwner()))
            .collect(Collectors.toList());
        if (emptyPlanets.isEmpty()) {
            throw new IllegalStateException("SolarSystem has no empty planets.");
        }
        return randomEmptyPlanet(emptyPlanets);
    }

    Planet randomEmptyPlanet(List<Planet> emptyPlanets) {
        return emptyPlanets.get(random.randInt(0, emptyPlanets.size() - 1));
    }
}
