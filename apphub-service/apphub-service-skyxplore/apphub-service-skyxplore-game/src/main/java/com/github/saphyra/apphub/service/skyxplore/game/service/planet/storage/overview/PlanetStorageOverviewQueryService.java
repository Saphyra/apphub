package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetStorageOverviewQueryService {
    private final GameDao gameDao;
    private final PlanetStorageDetailQueryService planetStorageDetailQueryService;

    public PlanetStorageResponse getStorage(UUID userId, UUID location) {
        GameData data = gameDao.findByUserIdValidated(userId)
            .getData();

        return getStorage(data, location);
    }

    public PlanetStorageResponse getStorage(GameData gameData, UUID location) {
        return PlanetStorageResponse.builder()
            .energy(planetStorageDetailQueryService.getStorageDetails(gameData, location, StorageType.ENERGY))
            .liquid(planetStorageDetailQueryService.getStorageDetails(gameData, location, StorageType.LIQUID))
            .bulk(planetStorageDetailQueryService.getStorageDetails(gameData, location, StorageType.CONTAINER))
            .build();
    }
}
