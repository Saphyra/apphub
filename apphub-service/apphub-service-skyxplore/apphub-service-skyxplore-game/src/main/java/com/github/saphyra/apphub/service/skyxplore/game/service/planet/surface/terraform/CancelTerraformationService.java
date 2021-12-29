package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.CancelAllocationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class CancelTerraformationService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final CancelAllocationsService cancelAllocationsService;

    void cancelTerraformation(UUID userId, UUID planetId, UUID surfaceId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByIdValidated(surfaceId);

        Construction construction = surface.getTerraformation();
        if (isNull(construction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Terraformation not found on planet " + planetId + " and surface " + surfaceId);
        }

        cancelAllocationsService.cancelAllocationsAndReservations(planet, construction.getConstructionId());

        surface.setTerraformation(null);
        gameDataProxy.deleteItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
    }
}
