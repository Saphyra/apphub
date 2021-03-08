package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class HomeSystemFinder {
    private final FreePlanetCounter freePlanetCounter;
    private final InhabitantFinder inhabitantFinder;

    Optional<SolarSystem> findHomeSystem(List<UUID> members, Universe universe) {
        return universe.getSystems()
            .values()
            .stream()
            .filter(solarSystem -> inhabitantFinder.getInhabitants(solarSystem).stream().anyMatch(members::contains))
            .max(Comparator.comparingLong(freePlanetCounter::getNumberOfFreePlanets));
    }
}
