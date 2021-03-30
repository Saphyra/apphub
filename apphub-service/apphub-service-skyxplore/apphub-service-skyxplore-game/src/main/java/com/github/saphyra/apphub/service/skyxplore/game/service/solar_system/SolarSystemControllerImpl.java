package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameSolarSystemController;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SolarSystemControllerImpl implements SkyXploreGameSolarSystemController {
    private final GameDao gameDao;

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    public SolarSystemResponse getSolarSystem(UUID solarSystemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to view solarSystem {}", accessTokenHeader.getUserId(), solarSystemId);

        Game game = gameDao.findByUserIdValidated(accessTokenHeader.getUserId());
        SolarSystem solarSystem = game.getUniverse()
            .getSystems()
            .values()
            .stream()
            .filter(solarSystem1 -> solarSystem1.getSolarSystemId().equals(solarSystemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("SolarSystem not found with id " + solarSystemId));

        List<PlanetLocationResponse> planets = mapPlanets(solarSystem.getPlanets().values());
        return SolarSystemResponse.builder()
            .solarSystemId(solarSystemId)
            .systemName(solarSystem.getDefaultName())
            .radius(solarSystem.getRadius())
            .planets(planets)
            .build();
    }

    private List<PlanetLocationResponse> mapPlanets(Collection<Planet> planets) {
        return planets.stream()
            .map(this::mapPlanet)
            .collect(Collectors.toList());
    }

    private PlanetLocationResponse mapPlanet(Planet planet) {
        return PlanetLocationResponse.builder()
            .planetId(planet.getPlanetId())
            .planetName(planet.getDefaultName())
            .coordinate(planet.getCoordinate())
            .build();
    }
}
