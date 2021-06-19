package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system.ClosestSystemFinder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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

    Planet findAllianceMemberSystem(List<UUID> members, Universe universe) {
        Optional<SolarSystem> allianceHomeSystemOptional = homeSystemFinder.findHomeSystem(members, universe);
        if (allianceHomeSystemOptional.isPresent()) {
            SolarSystem allianceHomeSystem = allianceHomeSystemOptional.get();
            if (freePlanetCounter.getNumberOfFreePlanets(allianceHomeSystem) == 0) {
                SolarSystem closestHabitableSystem = closestSystemFinder.getClosestSystemWithEmptyPlanet(allianceHomeSystem, universe);
                return randomEmptyPlanetFinder.randomEmptyPlanet(closestHabitableSystem);
            } else {
                return randomEmptyPlanetFinder.randomEmptyPlanet(allianceHomeSystem);
            }
        } else {
            return randomEmptyPlanetFinder.randomEmptyPlanet(universe);
        }
    }
}
