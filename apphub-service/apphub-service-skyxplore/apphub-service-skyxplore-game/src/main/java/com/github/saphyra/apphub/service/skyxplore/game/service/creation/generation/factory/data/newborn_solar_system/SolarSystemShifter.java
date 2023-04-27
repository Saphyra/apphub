package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
 class SolarSystemShifter {
    private final GameProperties gameProperties;

    /**
     * Shifting is necessary to have all the solar systems with positive-only coordinates, with a defined padding from the edge
     */
    Map<Coordinate, UUID[]> shiftSolarSystems(Map<Coordinate, UUID[]> placedSolarSystems) {
        int minX = getMin(placedSolarSystems.keySet(), Coordinate::getX);
        int minY = getMin(placedSolarSystems.keySet(), Coordinate::getY);

        int offsetX = getOffset(minX);
        int offsetY = getOffset(minY);

        Map<Coordinate, UUID[]> result = new HashMap<>();

        placedSolarSystems.forEach((coordinate, solarSystem) -> result.put(new Coordinate(coordinate.getX() + offsetX, coordinate.getY() + offsetY), solarSystem));

        return result;
    }

    private int getMin(Collection<Coordinate> coordinates, Function<Coordinate, Double> mapper) {
        double min = Integer.MAX_VALUE;
        for (Coordinate coordinate : coordinates) {
            double value = mapper.apply(coordinate);
            if (value < min) {
                min = value;
            }
        }

        return (int) min;
    }

    private int getOffset(int value) {
        int padding = gameProperties.getSolarSystem()
            .getPadding();

        if (value - padding < 0) {
            return Math.abs(value - padding);
        } else {
            return 0;
        }
    }
}
