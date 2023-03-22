package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO unit test
public class Planets extends ConcurrentHashMap<UUID, Planet> {
    public List<Planet> getBySolarSystemId(UUID solarSystemId) {
        return values()
            .stream()
            .filter(planet -> planet.getSolarSystemId().equals(solarSystemId))
            .collect(Collectors.toList());
    }
}
