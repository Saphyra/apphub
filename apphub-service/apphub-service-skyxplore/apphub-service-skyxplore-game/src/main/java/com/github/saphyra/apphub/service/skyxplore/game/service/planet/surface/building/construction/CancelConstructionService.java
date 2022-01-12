package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
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
public class CancelConstructionService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final CancelAllocationsService cancelAllocationsService;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final WsMessageSender messageSender;

    public void cancelConstructionOfConstruction(UUID userId, UUID planetId, UUID constructionId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .values()
            .stream()
            .filter(s -> nonNull(s.getBuilding()))
            .filter(s -> nonNull(s.getBuilding().getConstruction()))
            .filter(s -> s.getBuilding().getConstruction().getConstructionId().equals(constructionId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Construction not found with id " + constructionId));
        Building building = surface
            .getBuilding();

        processCancellation(planet, surface, building);

        SurfaceResponse surfaceResponse = surfaceToResponseConverter.convert(surface);
        messageSender.planetSurfaceModified(userId, planet.getPlanetId(), surfaceResponse);
    }

    public SurfaceResponse cancelConstructionOfBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByBuildingIdValidated(buildingId);
        Building building = surface
            .getBuilding();

        processCancellation(planet, surface, building);

        return surfaceToResponseConverter.convert(surface);
    }

    private void processCancellation(Planet planet, Surface surface, Building building) {
        Construction construction = building.getConstruction();
        if (isNull(construction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Construction not found on planet " + planet.getPlanetId() + " and building " + building.getBuildingId());
        }

        UUID constructionId = construction.getConstructionId();
        building.setConstruction(null);

        cancelAllocationsService.cancelAllocationsAndReservations(planet, constructionId);

        if (building.getLevel() == 0) {
            surface.setBuilding(null);
            gameDataProxy.deleteItem(building.getBuildingId(), GameItemType.BUILDING);
        }

        gameDataProxy.deleteItem(constructionId, GameItemType.CONSTRUCTION);
        messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), constructionId);
    }
}
