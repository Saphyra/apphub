package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGamePlanetOverviewController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.building_overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanetOverviewControllerImpl implements SkyXploreGamePlanetOverviewController {
    private final PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final SurfaceQueryService surfaceQueryService;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @Override
    public List<SurfaceResponse> getSurfaceOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the surface of planet {}", accessTokenHeader.getUserId(), planetId);
        return surfaceQueryService.getSurfaceOfPlanet(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    public PlanetStorageResponse getStorageOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storage of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetStorageOverviewQueryService.getStorage(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    public PlanetPopulationOverviewResponse getPopulationOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the population overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetPopulationOverviewQueryService.getPopulationOverview(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    public Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the building overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetBuildingOverviewQueryService.getBuildingOverview(accessTokenHeader.getUserId(), planetId);
    }
}
