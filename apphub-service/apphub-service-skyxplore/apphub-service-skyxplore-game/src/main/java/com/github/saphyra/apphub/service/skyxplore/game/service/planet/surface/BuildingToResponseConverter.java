package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingToResponseConverter {
    SurfaceBuildingResponse convert(Building building) {
        return Optional.ofNullable(building)
            .map(b -> SurfaceBuildingResponse.builder()
                .buildingId(b.getBuildingId())
                .dataId(b.getDataId())
                .level(b.getLevel())
                .build())
            .orElse(null);
    }
}
