package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetPlacerService {
    private final GameProperties gameProperties;
    private final Random random;

    NewbornSolarSystem placePlanets(Coordinate solarSystemCoordinate, UUID[] planets) {
        Range<Integer> distance = gameProperties.getSolarSystem()
            .getPlanetOrbitRadius();

        Map<Double, UUID> placedPlanets = new HashMap<>();

        int lastRadius = 0;
        for (UUID ownerId : planets) {
            int orbitRadius = lastRadius + random.randInt(distance);

            placedPlanets.put((double) orbitRadius, ownerId);
            lastRadius = orbitRadius;
        }

        return NewbornSolarSystem.builder()
            .coordinate(solarSystemCoordinate)
            .planets(placedPlanets)
            .radius(lastRadius + random.randInt(distance))
            .build();
    }
}
