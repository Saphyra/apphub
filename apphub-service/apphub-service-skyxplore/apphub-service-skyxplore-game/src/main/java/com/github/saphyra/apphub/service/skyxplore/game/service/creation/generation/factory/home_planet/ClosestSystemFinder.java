package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.home_planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClosestSystemFinder {
    private final DistanceCalculator distanceCalculator;
    private final FreePlanetCounter freePlanetCounter;

    public SolarSystem getClosestSystemWithEmptyPlanet(SolarSystem solarSystem, Map<Coordinate, SolarSystem> solarSystems) {
        Coordinate baseCoordinate = solarSystem.getCoordinate()
            .getCoordinate();
        return solarSystems.entrySet()
            .stream()
            .filter(entry -> !entry.getValue().equals(solarSystem))
            .filter(entry -> freePlanetCounter.getNumberOfFreePlanets(entry.getValue()) > 0)
            .min((o1, o2) -> (int) (distanceCalculator.getDistance(o1.getKey(), baseCoordinate) - distanceCalculator.getDistance(o2.getKey(), baseCoordinate)))
            .map(Map.Entry::getValue)
            .orElseThrow(() -> new RuntimeException("No empty planet left"));
    }
}
