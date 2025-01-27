package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.StorageDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetStorageOverviewQueryService {
    private static final StorageDetailsResponse DEFAULT_STORAGE_DETAILS = StorageDetailsResponse.builder()
        .capacity(0)
        .reservedStorageAmount(0)
        .actualResourceAmount(0)
        .allocatedResourceAmount(0)
        .resourceDetails(Collections.emptyList())
        .build();
    static final PlanetStorageResponse DEFAULT_RESPONSE = PlanetStorageResponse.builder()
        .energy(DEFAULT_STORAGE_DETAILS)
        .liquid(DEFAULT_STORAGE_DETAILS)
        .bulk(DEFAULT_STORAGE_DETAILS)
        .build();
    private final GameDao gameDao;
    private final PlanetStorageDetailQueryService planetStorageDetailQueryService;

    public PlanetStorageResponse getStorage(UUID userId, UUID location) {
        Optional<Game> maybeGame = gameDao.findByUserId(userId);

        if (maybeGame.isEmpty()) {
            return DEFAULT_RESPONSE;
        }
        GameData data = maybeGame.get()
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
