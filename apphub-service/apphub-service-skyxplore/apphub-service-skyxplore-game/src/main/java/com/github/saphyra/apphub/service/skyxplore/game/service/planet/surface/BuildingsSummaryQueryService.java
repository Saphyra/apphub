package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionAreaOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.ConstructionAreaOverviewQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BuildingsSummaryQueryService {
    private final GameDao gameDao;
    private final ConstructionAreaOverviewQueryService constructionAreaOverviewQueryService;

    public Map<String, List<ConstructionAreaOverviewResponse>> getBuildingsSummary(UUID userId, UUID planetId) {
        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();
        return gameData.getSurfaces()
            .getByPlanetId(planetId)
            .stream()
            .collect(Collectors.groupingBy(Surface::getSurfaceType))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().name(),
                entry -> constructionAreaOverviewQueryService.getConstructionAreaOverview(gameData, entry.getValue())
            ));
    }
}
