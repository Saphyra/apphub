package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.common_util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class EmptyPlanetSelector {
    private final Random random;

    int selectEmptyPlanet(UUID[] solarSystem) {
        List<Integer> availablePlanets = Stream.iterate(0, integer -> integer + 1)
            .limit(solarSystem.length)
            .filter(integer -> isNull(solarSystem[integer]))
            .toList();

        return availablePlanets.get(random.randInt(0, availablePlanets.size() - 1));
    }
}
