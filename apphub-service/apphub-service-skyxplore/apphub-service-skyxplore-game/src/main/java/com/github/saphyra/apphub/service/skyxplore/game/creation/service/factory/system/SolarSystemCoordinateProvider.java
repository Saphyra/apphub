package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SolarSystemCoordinateProvider {
    private final GameCreationProperties properties;
    private final Random random;
    private final DistanceCalculator distanceCalculator;

    public List<Coordinate> getCoordinates(int memberNum, int universeSize, SystemAmount systemAmount) {
        GameCreationProperties.SolarSystemProperties solarSystemProperties = properties.getSolarSystem();
        Double sizeMultiplier = solarSystemProperties
            .getSizeMultipliers()
            .get(systemAmount);
        int maxAllocationTryCount = (int) (universeSize / sizeMultiplier);
        int allocationTryCount = random.randInt(memberNum, maxAllocationTryCount);

        for (int overallTryCount = 0; overallTryCount < 10; overallTryCount++) {
            List<Coordinate> coordinates = new ArrayList<>();
            for (int tryCount = 0; tryCount < allocationTryCount; tryCount++) {
                Coordinate coordinate = new Coordinate(
                    new BigDecimal(random.randInt(0, universeSize)),
                    new BigDecimal(random.randInt(0, universeSize))
                );

                if (notTooClose(coordinate, coordinates)) {
                    coordinates.add(coordinate);
                }
            }

            if (coordinates.size() >= memberNum) {
                return coordinates;
            }
        }
        throw new RuntimeException("Could not generate the expected amount of coordinates.");
    }

    private boolean notTooClose(Coordinate coordinate, List<Coordinate> coordinates) {
        return coordinates.stream()
            .allMatch(c1 -> distanceCalculator.getDistance(c1, coordinate).doubleValue() >= properties.getSolarSystem().getMinSolarSystemDistance());
    }
}
