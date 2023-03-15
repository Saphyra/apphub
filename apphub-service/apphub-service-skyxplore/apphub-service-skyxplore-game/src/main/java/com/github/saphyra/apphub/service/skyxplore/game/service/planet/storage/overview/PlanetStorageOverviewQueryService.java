package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
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

    public PlanetStorageResponse getStorage(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        return getStorage(planet);
    }

    public PlanetStorageResponse getStorage(Planet planet) {
        log.debug("PlanetStorage: {}", planet.getStorageDetails());
        return PlanetStorageResponse.builder()
            .energy(planetStorageDetailQueryService.getStorageDetails(planet, StorageType.ENERGY))
            .liquid(planetStorageDetailQueryService.getStorageDetails(planet, StorageType.LIQUID))
            .bulk(planetStorageDetailQueryService.getStorageDetails(planet, StorageType.BULK))
            .build();
    }
}
