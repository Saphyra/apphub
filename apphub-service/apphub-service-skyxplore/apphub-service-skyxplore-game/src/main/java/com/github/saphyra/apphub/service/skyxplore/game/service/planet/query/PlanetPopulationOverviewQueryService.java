package com.github.saphyra.apphub.service.skyxplore.game.service.planet.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlanetPopulationOverviewQueryService {
    private final GameDao gameDao;
    private final StorageBuildingService storageBuildingService;

    public PlanetPopulationOverviewResponse getPopulationOverview(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        int population = planet.getPopulation().size();
        int capacity = calculateCapacity(planet);

        return PlanetPopulationOverviewResponse.builder()
            .population(population)
            .capacity(capacity)
            .build();
    }

    private int calculateCapacity(Planet planet) {
        String houseDataId = storageBuildingService.findByStorageType(StorageType.CITIZEN)
            .getId();

        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> houseDataId.equals(building.getDataId()))
            .map(building -> storageBuildingService.get(building.getDataId()))
            .mapToInt(StorageBuilding::getCapacity)
            .sum();
    }
}
