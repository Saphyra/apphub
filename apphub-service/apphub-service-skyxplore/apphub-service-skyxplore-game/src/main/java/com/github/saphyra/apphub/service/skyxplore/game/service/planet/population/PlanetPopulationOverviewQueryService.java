package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetPopulationOverviewQueryService {
    private final GameDao gameDao;
    private final StorageCalculator storageCalculator;

    public PlanetPopulationOverviewResponse getPopulationOverview(UUID userId, UUID planetId) {
        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();

        int population = gameData.getCitizens()
            .getByLocation(planetId)
            .size();
        int capacity = storageCalculator.calculateCapacity(gameData, planetId, StorageType.CITIZEN);

        return PlanetPopulationOverviewResponse.builder()
            .population(population)
            .capacity(capacity)
            .build();
    }
}
