package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelConstructionService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;

    public void cancelConstruction(UUID userId, UUID planetId, UUID buildingId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdAndOwnerValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByBuildingIdValidated(buildingId);
        Building building = surface
            .getBuilding();

        Construction construction = building.getConstruction();
        if (isNull(construction)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Construction not found on planet " + planetId + " and building " + buildingId);
        }

        UUID constructionId = construction.getConstructionId();
        building.setConstruction(null);

        StorageDetails storageDetails = planet.getStorageDetails();
        synchronized (storageDetails) {
            List<ReservedStorage> reservedStorages = storageDetails.getReservedStorages()
                .stream()
                .filter(reservedStorage -> reservedStorage.getExternalReference().equals(constructionId))
                .peek(reservedStorage -> gameDataProxy.deleteItem(reservedStorage.getReservedStorageId(), GameItemType.RESERVED_STORAGE))
                .collect(Collectors.toList());
            storageDetails.getReservedStorages()
                .removeAll(reservedStorages);

            List<AllocatedResource> allocatedResources = storageDetails.getAllocatedResources()
                .stream()
                .filter(allocatedResource -> allocatedResource.getExternalReference().equals(constructionId))
                .peek(allocatedResource -> gameDataProxy.deleteItem(allocatedResource.getAllocatedResourceId(), GameItemType.ALLOCATED_RESOURCE))
                .collect(Collectors.toList());
            storageDetails.getAllocatedResources()
                .removeAll(allocatedResources);

            if (building.getLevel() == 0) {
                surface.setBuilding(null);
                gameDataProxy.deleteItem(buildingId, GameItemType.BUILDING);
            }
        }

        gameDataProxy.deleteItem(constructionId, GameItemType.CONSTRUCTION);
    }
}
