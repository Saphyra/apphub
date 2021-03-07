package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class FreePlanetCounter {
    long getNumberOfFreePlanets(SolarSystem solarSystem) {
        return solarSystem.getPlanets()
            .values()
            .stream()
            .filter(planet -> isNull(planet.getOwner()))
            .count();

    }
}
