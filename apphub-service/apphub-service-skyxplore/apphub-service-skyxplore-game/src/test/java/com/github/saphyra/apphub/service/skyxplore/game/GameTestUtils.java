package com.github.saphyra.apphub.service.skyxplore.game;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;

public class GameTestUtils {
    private static final Random RANDOM = new Random();

    public static Coordinate randomCoordinate() {
        return new Coordinate(RANDOM.randDouble(), RANDOM.randDouble());
    }
}
