package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.CancelAllocationsService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelTerraformationService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final CancelAllocationsService cancelAllocationsService;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;

    public void cancelTerraformationOfConstruction(UUID userId, UUID planetId, UUID constructionId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .values()
            .stream()
            .filter(s -> nonNull(s.getTerraformation()))
            .filter(s -> s.getTerraformation().getConstructionId().equals(constructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Surface not found by terraformation constructionId " + constructionId));

        processCancellation(planet, surface);
        SurfaceResponse surfaceResponse = surfaceToResponseConverter.convert(surface);
        messageSender.planetSurfaceModified(userId, planetId, surfaceResponse);
    }

    SurfaceResponse cancelTerraformationOfSurface(UUID userId, UUID planetId, UUID surfaceId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByIdValidated(surfaceId);

        processCancellation(planet, surface);
        return surfaceToResponseConverter.convert(surface);
    }

    private void processCancellation(Planet planet, Surface surface) {
        Construction construction = surface.getTerraformation();
        if (isNull(construction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Terraformation not found on planet " + planet.getPlanetId() + " and surface " + surface.getSurfaceId());
        }

        cancelAllocationsService.cancelAllocationsAndReservations(planet, construction.getConstructionId());

        surface.setTerraformation(null);
        gameDataProxy.deleteItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
        messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId());
    }
}
