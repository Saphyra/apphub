package com.github.saphyra.apphub.lib.geometry;

import com.github.saphyra.apphub.lib.common_util.Random;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RandomCoordinateProvider {
    private final Random random;

    public Coordinate getCoordinateInCircle(double systemRadius) {
        double randomDistance = random.randDouble() * 2 * Math.PI;
        double r = systemRadius * Math.sqrt(random.randDouble());

        return new Coordinate(
            Math.floor(Math.cos(randomDistance) * r),
            Math.floor(Math.sin(randomDistance) * r)
        );
    }

    public Coordinate getCoordinateInSquare(int universeSize) {
        return new Coordinate(random.randInt(0, universeSize), random.randInt(0, universeSize));
    }
}
