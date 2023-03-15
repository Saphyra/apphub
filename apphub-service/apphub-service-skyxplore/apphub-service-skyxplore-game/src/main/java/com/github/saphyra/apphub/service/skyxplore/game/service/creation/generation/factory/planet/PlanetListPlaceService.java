package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetListPlaceService {
    private final PlanetPlaceService planetPlaceService;

    List<Coordinate> placePlanets(int expectedPlanetAmount, int systemRadius) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int planetNumber = 0; planetNumber < expectedPlanetAmount; planetNumber++) {
            log.debug("Generating planet #{}...", planetNumber);

            planetPlaceService.placePlanet(systemRadius, coordinates)
                .ifPresent(coordinates::add);

        }
        return coordinates;
    }
}
