package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Planets extends ConcurrentHashMap<UUID, Planet> {
    public List<Planet> getBySolarSystemId(UUID solarSystemId) {
        return values()
            .stream()
            .filter(planet -> planet.getSolarSystemId().equals(solarSystemId))
            .collect(Collectors.toList());
    }

    public Planet findByIdValidated(UUID planetId) {
        return values()
            .stream()
            .filter(planet -> planet.getPlanetId().equals(planetId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Planet not found with id " + planetId));
    }
}
