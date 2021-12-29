package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetBuildingOverviewMapper {
    private final BuildingDetailsMapper buildingDetailsMapper;

    PlanetBuildingOverviewResponse createOverview(List<Surface> surfaces) {
        int usedSlots = (int) surfaces.stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .count();

        return PlanetBuildingOverviewResponse.builder()
            .buildingDetails(buildingDetailsMapper.createBuildingDetails(surfaces))
            .slots(surfaces.size())
            .usedSlots(usedSlots)
            .build();
    }
}
