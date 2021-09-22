package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemResponseQueryService {
    private final GameDao gameDao;
    private final PlanetToLocationResponseConverter planetToLocationResponseConverter;
    private final VisibilityFacade visibilityFacade;

    SolarSystemResponse getSolarSystem(UUID userId, UUID solarSystemId) {
        Game game = gameDao.findByUserIdValidated(userId);
        SolarSystem solarSystem = game.getUniverse()
            .getSystems()
            .values()
            .stream()
            .filter(solarSystem1 -> solarSystem1.getSolarSystemId().equals(solarSystemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("SolarSystem not found with id " + solarSystemId));

        if (!visibilityFacade.isVisible(userId, solarSystem)) {
            //TODO throw exception
        }

        List<PlanetLocationResponse> planets = planetToLocationResponseConverter.mapPlanets(userId, solarSystem.getPlanets().values(), game);
        return SolarSystemResponse.builder()
            .solarSystemId(solarSystemId)
            .systemName(solarSystem.getCustomNames().getOptional(userId).orElse(solarSystem.getDefaultName()))
            .radius(solarSystem.getRadius())
            .planets(planets)
            .build();
    }
}
