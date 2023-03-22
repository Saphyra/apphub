package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.NewbornSolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class UniverseSizeCalculator {
    public int calculate(List<NewbornSolarSystem> newbornSolarSystems) {
        int maxX = getMax(newbornSolarSystems, Coordinate::getX);
        int maxY = getMax(newbornSolarSystems, Coordinate::getY);

        return Math.max(maxX, maxY);
    }

    private int getMax(List<NewbornSolarSystem> newbornSolarSystems, Function<Coordinate, Double> mapper) {
        int max = Integer.MIN_VALUE;
        for (NewbornSolarSystem solarSystem : newbornSolarSystems) {
            int value = mapper.apply(solarSystem.getCoordinate()).intValue();
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
