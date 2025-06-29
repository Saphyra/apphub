package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetPopulationOverviewQueryService {
    private final GameDao gameDao;
    private final StorageCapacityService storageCapacityService;

    public PlanetPopulationOverviewResponse getPopulationOverview(UUID userId, UUID planetId) {
        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();

        int population = gameData.getCitizens()
            .getByLocation(planetId)
            .size();
        int capacity = storageCapacityService.calculateDwellingCapacity(gameData, planetId);

        return PlanetPopulationOverviewResponse.builder()
            .population(population)
            .capacity(capacity)
            .build();
    }
}
