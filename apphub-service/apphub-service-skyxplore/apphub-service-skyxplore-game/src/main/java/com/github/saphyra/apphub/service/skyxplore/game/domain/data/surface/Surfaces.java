package com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface;

import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class Surfaces extends Vector<Surface> {
    public Collection<Surface> getByPlanetId(UUID planetId) {
        return stream()
            .filter(surface -> surface.getPlanetId().equals(planetId))
            .toList();
    }
}
