package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
class SolarSystemCoordinateProvider {
    private final GameCreationProperties properties;
    private final Random random;
    private final DistanceCalculator distanceCalculator;

    public List<Coordinate> getCoordinates(int memberNum, int universeSize, SystemAmount systemAmount) {
        log.info("Placing systems...");
        GameCreationProperties.SolarSystemProperties solarSystemProperties = properties.getSolarSystem();

        SystemAmount amount = systemAmount == SystemAmount.RANDOM ? SystemAmount.values()[random.randInt(0, 2)] : systemAmount;
        Double sizeMultiplier = solarSystemProperties
            .getSizeMultiplier()
            .get(amount);
        int maxAllocationTryCount = (int) (universeSize * sizeMultiplier);

        for (int overallTryCount = 0; overallTryCount < 10; overallTryCount++) {
            log.info("Placing systems. TryCount: {}", overallTryCount);
            int allocationTryCount = random.randInt(Math.max(memberNum + 1, maxAllocationTryCount / 2), maxAllocationTryCount);
            log.info("AllocationTryCount: {}. MemberNum: {}, maxAllocationTryCount: {}. UniverseSize: {}, sizeMultiplier: {}", allocationTryCount, memberNum, maxAllocationTryCount, universeSize, sizeMultiplier);
            List<Coordinate> coordinates = new ArrayList<>();
            for (int tryCount = 0; tryCount < allocationTryCount; tryCount++) {
                Coordinate coordinate = new Coordinate(
                    random.randInt(0, universeSize),
                    random.randInt(0, universeSize)
                );

                if (notTooClose(coordinate, coordinates)) {
                    coordinates.add(coordinate);
                } else {
                    log.debug("Could not place system: Another one is too close.");
                }
            }

            if (coordinates.size() >= memberNum + 1) {
                log.info("Number of generated systems: {}", coordinates.size());
                return coordinates;
            }
        }
        throw new RuntimeException("Could not generate the expected amount of coordinates.");
    }

    private boolean notTooClose(Coordinate coordinate, List<Coordinate> coordinates) {
        return coordinates.stream()
            .allMatch(c1 -> distanceCalculator.getDistance(c1, coordinate) >= properties.getSolarSystem().getMinSolarSystemDistance());
    }
}
