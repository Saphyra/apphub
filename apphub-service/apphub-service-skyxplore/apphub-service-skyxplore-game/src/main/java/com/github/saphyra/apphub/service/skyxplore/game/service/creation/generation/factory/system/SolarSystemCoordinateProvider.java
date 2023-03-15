package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.system;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemCoordinateProvider {
    private final GameProperties gameCreationProperties;
    private final Random random;
    private final RandomCoordinateProvider randomCoordinateProvider;
    private final DistanceCalculator distanceCalculator;

    public Coordinate getCoordinate(List<SolarSystem> solarSystems) {
        Range<Integer> distanceRange = gameCreationProperties.getSolarSystem()
            .getSolarSystemDistance();

        for (int i = 0; i < 10000; i++) {
            Coordinate anchor = solarSystems.isEmpty() ? GameConstants.ORIGO : solarSystems.get(random.randInt(0, solarSystems.size() - 1)).getCoordinate().getCoordinate();

            Coordinate generated = generateCoordinate(distanceRange)
                .add(anchor);

            boolean placeable = solarSystems.stream()
                .mapToDouble(solarSystem -> distanceCalculator.getDistance(generated, solarSystem.getCoordinate().getCoordinate()))
                .allMatch(value -> value > distanceRange.getMin());
            if (placeable) {
                return generated;
            }
        }

        throw new RuntimeException("Failed generating coordinate. It was too close to an other SolarSystem");
    }

    private Coordinate generateCoordinate(Range<Integer> distanceRange) {
        for (int i = 0; i < 1000; i++) {
            Coordinate coordinate = randomCoordinateProvider.getCoordinateInCircle(distanceRange.getMax());
            if (distanceCalculator.getDistance(coordinate, GameConstants.ORIGO) > distanceRange.getMin()) {
                return coordinate;
            }
        }

        throw new RuntimeException("Failed generating coordinate. It was too close to the anchor SolarSystem");
    }
}
