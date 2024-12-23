package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingDetailMapper {
    PlanetBuildingOverviewDetailedResponse createBuildingDetail(GameData gameData, String dataId, List<Building> buildings) {
        int levelSum = buildings.stream()
            .filter(building -> gameData.getDeconstructions().findByExternalReference(building.getBuildingId()).isEmpty())
            .mapToInt(Building::getLevel)
            .sum();

        return PlanetBuildingOverviewDetailedResponse.builder()
            .dataId(dataId)
            .levelSum(levelSum)
            .usedSlots(buildings.size())
            .build();
    }
}
