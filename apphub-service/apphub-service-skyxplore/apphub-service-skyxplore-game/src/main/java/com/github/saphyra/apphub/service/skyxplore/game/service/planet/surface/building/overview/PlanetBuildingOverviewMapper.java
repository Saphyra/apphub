package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetBuildingOverviewMapper {
    private final BuildingDetailsMapper buildingDetailsMapper;

    PlanetBuildingOverviewResponse createOverview(GameData gameData, UUID location, SurfaceType surfaceType) {
        int usedSlots = gameData.getBuildings()
            .getByLocation(location)
            .size();

        int planetSize = gameData.getPlanets()
            .get(location)
            .getSize();

        return PlanetBuildingOverviewResponse.builder()
            .buildingDetails(buildingDetailsMapper.createBuildingDetails(gameData, location, surfaceType))
            .slots(planetSize)
            .usedSlots(usedSlots)
            .build();
    }
}
