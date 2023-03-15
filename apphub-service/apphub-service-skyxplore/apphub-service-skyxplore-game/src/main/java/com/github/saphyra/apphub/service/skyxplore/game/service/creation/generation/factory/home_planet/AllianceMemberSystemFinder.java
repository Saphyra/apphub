package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.home_planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class AllianceMemberSystemFinder {
    private final HomeSystemFinder homeSystemFinder;
    private final RandomEmptyPlanetFinder randomEmptyPlanetFinder;
    private final FreePlanetCounter freePlanetCounter;
    private final ClosestSystemFinder closestSystemFinder;

    Planet findAllianceMemberSystem(List<UUID> members, Map<Coordinate, SolarSystem> solarSystems) {
        Optional<SolarSystem> allianceHomeSystemOptional = homeSystemFinder.findHomeSystem(members, solarSystems.values());
        if (allianceHomeSystemOptional.isPresent()) {
            SolarSystem allianceHomeSystem = allianceHomeSystemOptional.get();
            if (freePlanetCounter.getNumberOfFreePlanets(allianceHomeSystem) == 0) {
                SolarSystem closestHabitableSystem = closestSystemFinder.getClosestSystemWithEmptyPlanet(allianceHomeSystem, solarSystems);
                return randomEmptyPlanetFinder.randomEmptyPlanet(closestHabitableSystem);
            } else {
                return randomEmptyPlanetFinder.randomEmptyPlanet(allianceHomeSystem);
            }
        } else {
            return randomEmptyPlanetFinder.randomEmptyPlanet(solarSystems.values());
        }
    }
}
