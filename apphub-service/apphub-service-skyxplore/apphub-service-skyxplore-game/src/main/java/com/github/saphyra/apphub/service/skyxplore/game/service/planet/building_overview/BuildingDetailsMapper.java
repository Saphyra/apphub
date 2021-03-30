package com.github.saphyra.apphub.service.skyxplore.game.service.planet.building_overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingDetailsMapper {
    private final BuildingDetailMapper buildingDetailMapper;

    List<PlanetBuildingOverviewDetailedResponse> createBuildingDetails(List<Surface> surfaces) {
        return surfaces.stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .collect(Collectors.groupingBy(Building::getDataId))
            .entrySet()
            .stream()
            .map(entry -> buildingDetailMapper.createBuildingDetail(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}