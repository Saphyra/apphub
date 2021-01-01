package com.github.saphyra.apphub.service.skyxplore.game.controller;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGamePlanetController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.query.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.query.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.query.PlanetStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.query.StorageSettingsQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.query.SurfaceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanetControllerImpl implements SkyXploreGamePlanetController {
    private final GameDao gameDao;
    private final PlanetStorageQueryService planetStorageQueryService;
    private final PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final StorageSettingsQueryService storageSettingsQueryService;
    private final SurfaceQueryService surfaceQueryService;

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public List<SurfaceResponse> getSurfaceOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the surface of planet {}", accessTokenHeader.getUserId(), planetId);
        return surfaceQueryService.getSurfaceOfPlanet(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public PlanetStorageResponse getStorageOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storage of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetStorageQueryService.getStorage(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public PlanetPopulationOverviewResponse getPopulationOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the population overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetPopulationOverviewQueryService.getPopulationOverview(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the building overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetBuildingOverviewQueryService.getBuildingOverview(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public Map<String, Integer> getPriorities(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the priorities of planet {}", accessTokenHeader.getUserId(), planetId);

        return gameDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPriorities()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public StorageSettingsResponse getStorageSettings(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storageSettings of planet {}", accessTokenHeader.getUserId(), planetId);
        return storageSettingsQueryService.getStorageSettings(accessTokenHeader.getUserId(), planetId);
    }
}
