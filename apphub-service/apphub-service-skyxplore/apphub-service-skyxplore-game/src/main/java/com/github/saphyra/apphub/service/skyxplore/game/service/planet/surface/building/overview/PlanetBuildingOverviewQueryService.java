package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetBuildingOverviewQueryService {
    private final GameDao gameDao;
    private final PlanetBuildingOverviewMapper overviewMapper;

    public Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);
        return getBuildingOverview(planet);
    }

    public Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(Planet planet) {
        return planet
            .getSurfaces()
            .values()
            .stream()
            .collect(Collectors.groupingBy(Surface::getSurfaceType))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(surfaceTypeListEntry -> surfaceTypeListEntry.getKey().name(), o -> overviewMapper.createOverview(o.getValue())));
    }
}
