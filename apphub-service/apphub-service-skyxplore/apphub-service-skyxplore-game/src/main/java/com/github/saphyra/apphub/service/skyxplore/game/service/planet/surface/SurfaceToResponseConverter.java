package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceToResponseConverter {
    private final BuildingToResponseConverter buildingToResponseConverter;

    SurfaceResponse convert(Surface surface) {
        return SurfaceResponse.builder()
            .surfaceId(surface.getSurfaceId())
            .coordinate(surface.getCoordinate())
            .surfaceType(surface.getSurfaceType().name())
            .building(buildingToResponseConverter.convert(surface.getBuilding()))
            .build();
    }
}
