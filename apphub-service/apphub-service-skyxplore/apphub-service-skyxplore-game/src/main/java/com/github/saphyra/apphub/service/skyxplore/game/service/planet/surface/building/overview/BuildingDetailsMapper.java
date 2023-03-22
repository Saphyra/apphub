package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingDetailsMapper {
    private final BuildingDetailMapper buildingDetailMapper;

    List<PlanetBuildingOverviewDetailedResponse> createBuildingDetails(GameData gameData, UUID planetId) {
        return gameData.getBuildings()
            .getByLocation(planetId)
            .stream()
            .collect(Collectors.groupingBy(Building::getDataId))
            .entrySet()
            .stream()
            .map(entry -> buildingDetailMapper.createBuildingDetail(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}