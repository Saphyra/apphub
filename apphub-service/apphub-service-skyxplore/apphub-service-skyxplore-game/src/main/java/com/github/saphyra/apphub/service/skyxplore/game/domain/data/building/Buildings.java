package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class Buildings extends Vector<Building> {
    public Optional<Building> findBySurfaceId(UUID surfaceId) {
        return stream()
            .filter(building -> building.getSurfaceId().equals(surfaceId))
            .findFirst();
    }
}
