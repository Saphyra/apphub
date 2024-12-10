package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();
        return getBuildingOverview(gameData, planetId);
    }

    public Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(GameData gameData, UUID planetId) {
        return gameData.getSurfaces()
            .getByPlanetId((planetId))
            .stream()
            .map(Surface::getSurfaceType)
            .distinct()
            .collect(Collectors.toMap(Enum::name, surfaceType -> overviewMapper.createOverview(gameData, planetId, surfaceType)));
    }
}
