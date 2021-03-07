package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class InhabitantFinder {
    List<UUID> getInhabitants(SolarSystem solarSystem) {
        return solarSystem.getPlanets()
            .values()
            .stream()
            .filter(planet -> !isNull(planet.getOwner()))
            .map(Planet::getOwner)
            .collect(Collectors.toList());
    }
}
