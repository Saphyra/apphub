package com.github.saphyra.apphub.service.skyxplore.game.controller;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGamePlanetController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.query.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.query.PlanetStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanetControllerImpl implements SkyXploreGamePlanetController {
    private final GameDao gameDao;
    private final PlanetStorageQueryService planetStorageQueryService;
    private final PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public List<SurfaceResponse> getSurfaceOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the surface of planet {}", accessTokenHeader.getUserId(), planetId);
        return gameDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getUniverse()
            .findPlanetById(planetId)
            .orElseThrow(() -> new RuntimeException("Planet not found with id " + planetId))
            .getSurfaces()
            .values()
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    @Override
    public PlanetStorageResponse getStorageOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storage of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetStorageQueryService.getStorage(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    public PlanetPopulationOverviewResponse getPopulationOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the population overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetPopulationOverviewQueryService.getPopulationOverview(accessTokenHeader.getUserId(), planetId);
    }

    private SurfaceResponse convert(Surface surface) {
        return SurfaceResponse.builder()
            .surfaceId(surface.getSurfaceId())
            .coordinate(surface.getCoordinate())
            .surfaceType(surface.getSurfaceType().name())
            .building(convert(surface.getBuilding()))
            .build();
    }

    private SurfaceBuildingResponse convert(Building building) {
        return Optional.ofNullable(building)
            .map(b -> SurfaceBuildingResponse.builder()
                .buildingId(b.getBuildingId())
                .dataId(b.getDataId())
                .level(b.getLevel())
                .build())
            .orElse(null);
    }
}
