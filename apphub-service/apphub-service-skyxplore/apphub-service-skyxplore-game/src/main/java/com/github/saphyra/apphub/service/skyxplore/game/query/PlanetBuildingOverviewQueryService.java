package com.github.saphyra.apphub.service.skyxplore.game.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlanetBuildingOverviewQueryService {
    private final GameDao gameDao;

    public Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        Map<SurfaceType, List<Surface>> surfaceMapping = planet.getSurfaces()
            .values()
            .stream()
            .collect(Collectors.groupingBy(Surface::getSurfaceType));

        return surfaceMapping.entrySet()
            .stream()
            .collect(Collectors.toMap(surfaceTypeListEntry -> surfaceTypeListEntry.getKey().name(), o -> createOverview(o.getValue())));
    }

    private PlanetBuildingOverviewResponse createOverview(List<Surface> surfaces) {
        int usedSlots = (int) surfaces.stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .count();

        return PlanetBuildingOverviewResponse.builder()
            .buildingDetails(createBuildingDetails(surfaces))
            .slots(surfaces.size())
            .usedSlots(usedSlots)
            .build();
    }

    private List<PlanetBuildingOverviewDetailedResponse> createBuildingDetails(List<Surface> surfaces) {
        Map<String, List<Building>> buildingTypes = surfaces.stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .collect(Collectors.groupingBy(Building::getDataId));

        return buildingTypes.keySet()
            .stream()
            .map(dataId -> createBuildingDetail(dataId, buildingTypes.get(dataId)))
            .collect(Collectors.toList());
    }

    private PlanetBuildingOverviewDetailedResponse createBuildingDetail(String dataId, List<Building> buildings) {
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
