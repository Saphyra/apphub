package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetOverviewQueryService {
    private final GameDao gameDao;

    PlanetOverviewResponse getOverview(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        return PlanetOverviewResponse.builder()
            .planetName(getPlanetName(userId, planet))
            .build();
    }

    private String getPlanetName(UUID userId, Planet planet) {
        return planet.getCustomNames().getOrDefault(userId, planet.getDefaultName());
    }
}
