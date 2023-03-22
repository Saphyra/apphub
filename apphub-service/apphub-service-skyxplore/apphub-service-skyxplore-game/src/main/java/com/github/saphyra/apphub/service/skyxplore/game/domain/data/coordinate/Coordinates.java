package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.geometry.Coordinate;

import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class Coordinates extends Vector<ReferredCoordinate> {
    public Coordinate findByReferenceId(UUID surfaceId) {
        return stream()
            .filter(referredCoordinate -> referredCoordinate.getReferenceId().equals(surfaceId))
            .findFirst()
            .orElseThrow()
            .getCoordinate();
    }
}
