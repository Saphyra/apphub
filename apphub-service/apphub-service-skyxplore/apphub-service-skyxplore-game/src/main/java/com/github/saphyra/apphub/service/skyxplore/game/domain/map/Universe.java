package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
//TODO unit test
public class Universe {
    private final int size;
    private final Map<Coordinate, SolarSystem> systems;
    private final List<SystemConnection> connections;

    public Optional<Planet> findPlanetById(UUID planetId) {
        return systems.values()
            .stream()
            .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
            .filter(planet -> planet.getPlanetId().equals(planetId))
            .findFirst();
    }

    public Planet findPlanetByIdValidated(UUID planetId) {
        return findPlanetById(planetId)
            .orElseThrow(() -> new RuntimeException("Planet not found with id " + planetId)); //TODO proper exception
    }
}
