package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
class HomePlanetSelector {
    private final Random random;
    private final DistanceCalculator distanceCalculator;

    public Planet selectPlanet(UUID userId, Collection<Alliance> alliances, Universe universe) {
        Optional<Alliance> allianceOptional = findAlliance(userId, alliances);
        if (allianceOptional.isPresent()) {
            List<UUID> members = allianceOptional.get()
                .getMembers()
                .values()
                .stream()
                .map(Player::getUserId)
                .collect(Collectors.toList());

            return findAllianceMemberSystem(members, universe);
        } else {
            return randomEmptyPlanet(universe);
        }
    }

    private Planet findAllianceMemberSystem(List<UUID> members, Universe universe) {
        Optional<SolarSystem> allianceHomeSystemOptional = findHomeSystem(members, universe);
        if (allianceHomeSystemOptional.isPresent()) {
            SolarSystem allianceHomeSystem = allianceHomeSystemOptional.get();
            if (getNumberOfFreePlanets(allianceHomeSystem) == 0) {
                SolarSystem closestHabitableSystem = getClosestSystemWithEmptyPlanet(allianceHomeSystem, universe, Arrays.asList(allianceHomeSystem.getCoordinate()))
                    .getEntity1();
                return randomEmptyPlanet(closestHabitableSystem);
            } else {
                return randomEmptyPlanet(allianceHomeSystem);
            }
        } else {
            List<Planet> emptyPlanets = universe.getSystems()
                .values()
                .stream()
                .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
                .filter(planet -> isNull(planet.getOwner()))
                .collect(Collectors.toList());
            return randomEmptyPlanet(emptyPlanets);
        }
    }

    private BiWrapper<SolarSystem, Double> getClosestSystemWithEmptyPlanet(SolarSystem solarSystem, Universe universe, List<Coordinate> route) {
        List<Line> connections = universe.getConnections()
            .stream()
            .map(SystemConnection::getLine)
            .filter(systemConnection -> systemConnection.isEndpoint(solarSystem.getCoordinate()))
            .filter(systemConnection -> !route.contains(systemConnection.getA()) && !route.contains(systemConnection.getB()))
            .sorted((o1, o2) -> Double.compare(o2.getLength(distanceCalculator), o1.getLength(distanceCalculator)))
            .collect(Collectors.toList());

        Optional<SolarSystem> closestSystemWithEmptyPlanetOptional = connections.stream()
            .map(line -> line.getOtherEndpoint(solarSystem.getCoordinate()))
            .map(coordinate -> findSystemByCoordinate(coordinate, universe.getSystems().values()))
            .filter(o -> hasEmptyPlanet(solarSystem))
            .findFirst();

        if (closestSystemWithEmptyPlanetOptional.isPresent()) {
            SolarSystem closestSystemWithEmptyPlanet = closestSystemWithEmptyPlanetOptional.get();
            List<Coordinate> newRoute = new ArrayList<>(route);
            newRoute.add(closestSystemWithEmptyPlanet.getCoordinate());
            return BiWrapper.<SolarSystem, Double>builder()
                .entity1(closestSystemWithEmptyPlanet)
                .entity2(getRouteLength(newRoute))
                .build();
        } else {
            return connections.stream()
                .map(line -> line.getOtherEndpoint(solarSystem.getCoordinate()))
                .map(coordinate -> findSystemByCoordinate(coordinate, universe.getSystems().values()))
                .map(system -> {
                    List<Coordinate> newRoute = new ArrayList<>(route);
                    newRoute.add(system.getCoordinate());
                    return getClosestSystemWithEmptyPlanet(system, universe, newRoute);
                }).min(Comparator.comparingDouble(BiWrapper::getEntity2))
                .orElseThrow(() -> new RuntimeException("No empty planet left."));
        }
    }

    private Double getRouteLength(List<Coordinate> route) {
        double result = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            Coordinate c1 = route.get(i);
            Coordinate c2 = route.get(i + 1);
            result += distanceCalculator.getDistance(c1, c2);
        }
        return result;
    }

    private boolean hasEmptyPlanet(SolarSystem solarSystem) {
        return solarSystem.getPlanets()
            .values()
            .stream()
            .anyMatch(planet -> isNull(planet.getOwner()));
    }

    private SolarSystem findSystemByCoordinate(Coordinate coordinate, Collection<SolarSystem> systems) {
        return systems.stream()
            .filter(solarSystem -> solarSystem.getCoordinate().equals(coordinate))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No solarSystem present on coordinate " + coordinate));
    }

    private Planet randomEmptyPlanet(SolarSystem allianceHomeSystem) {
        List<Planet> emptyPlanets = allianceHomeSystem.getPlanets()
            .values()
            .stream()
            .filter(planet -> isNull(planet.getOwner()))
            .collect(Collectors.toList());
        if (emptyPlanets.isEmpty()) {
            throw new IllegalStateException("Planet has no empty planets.");
        }
        return randomEmptyPlanet(emptyPlanets);
    }

    private Optional<SolarSystem> findHomeSystem(List<UUID> members, Universe universe) {
        return universe.getSystems()
            .values()
            .stream()
            .filter(solarSystem -> getInhabitants(solarSystem).stream().anyMatch(members::contains))
            .max(Comparator.comparingLong(this::getNumberOfFreePlanets));
    }

    private long getNumberOfFreePlanets(SolarSystem solarSystem) {
        return solarSystem.getPlanets()
            .values()
            .stream()
            .filter(planet -> isNull(planet.getOwner()))
            .count();
    }

    private List<UUID> getInhabitants(SolarSystem solarSystem) {
        return solarSystem.getPlanets()
            .values()
            .stream()
            .filter(planet -> !isNull(planet.getOwner()))
            .map(Planet::getOwner)
            .collect(Collectors.toList());
    }

    private Planet randomEmptyPlanet(Universe universe) {
        List<Planet> emptyPlanets = universe.getSystems()
            .values()
            .stream()
            .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
            .filter(planet -> isNull(planet.getOwner()))
            .collect(Collectors.toList());

        return randomEmptyPlanet(emptyPlanets);
    }

    private Optional<Alliance> findAlliance(UUID userId, Collection<Alliance> alliances) {
        return alliances.stream()
            .filter(alliance -> alliance.getMembers().containsKey(userId))
            .findFirst();
    }

    private Planet randomEmptyPlanet(List<Planet> emptyPlanets) {
        return emptyPlanets.get(random.randInt(0, emptyPlanets.size() - 1));
    }
}
