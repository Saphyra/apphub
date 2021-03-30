package com.github.saphyra.apphub.service.skyxplore.game.service.planet.building_overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingDetailMapper {
    PlanetBuildingOverviewDetailedResponse createBuildingDetail(String dataId, List<Building> buildings) {
        int levelSum = buildings.stream()
            .mapToInt(Building::getLevel)
            .sum();

        return PlanetBuildingOverviewDetailedResponse.builder()
            .dataId(dataId)
            .levelSum(levelSum)
            .usedSlots(buildings.size())
            .build();
    }
}
