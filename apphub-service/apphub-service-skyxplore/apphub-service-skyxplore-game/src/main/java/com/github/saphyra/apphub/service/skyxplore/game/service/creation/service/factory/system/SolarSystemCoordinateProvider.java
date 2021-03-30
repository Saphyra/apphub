package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemCoordinateProvider {
    private final Random random;
    private final SolarSystemCoordinateListProvider solarSystemCoordinateListProvider;
    private final MaxSystemCountCalculator maxSystemCountCalculator;

    public List<Coordinate> getCoordinates(int memberNum, int universeSize, SystemAmount systemAmount) {
        log.info("Placing systems...");
        int maxAllocationTryCount = maxSystemCountCalculator.getMaxAllocationTryCount(universeSize, systemAmount);

        return getCoordinates(memberNum, universeSize, maxAllocationTryCount);
    }

    private List<Coordinate> getCoordinates(int memberNum, int universeSize, int maxAllocationTryCount) {
        for (int overallTryCount = 0; overallTryCount < 10; overallTryCount++) {
            log.info("Placing systems. TryCount: {}", overallTryCount);
            int allocationTryCount = random.randInt(Math.max(memberNum + 1, maxAllocationTryCount / 2), maxAllocationTryCount);
            log.info("AllocationTryCount: {}. MemberNum: {}, maxAllocationTryCount: {}. UniverseSize: {}", allocationTryCount, memberNum, maxAllocationTryCount, universeSize);
            List<Coordinate> coordinates = solarSystemCoordinateListProvider.getCoordinates(universeSize, allocationTryCount);

            if (coordinates.size() >= memberNum + 1) {
                log.info("Number of generated systems: {}", coordinates.size());
                return coordinates;
            }
        }
        throw new RuntimeException("Could not generate the expected amount of coordinates.");
    }
}
