package com.github.saphyra.apphub.service.skyxplore.game.service.planet.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SurfaceQueryService {
    private final GameDao gameDao;

    public List<SurfaceResponse> getSurfaceOfPlanet(UUID userId, UUID planetId) {
        return gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetById(planetId)
            .orElseThrow(() -> new RuntimeException("Planet not found with id " + planetId))
            .getSurfaces()
            .values()
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
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
