package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CoordinateFinder {
    Coordinate getCoordinateByStoredResource(GameData gameData, StoredResource storedResource) {
        return getCoordinateByIdAndContainerType(gameData, storedResource.getContainerId(), storedResource.getContainerType());
    }

    Coordinate getCoordinateByReservedStorageId(GameData gameData, UUID reservedStorageId) {
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByIdValidated(reservedStorageId);

        return getCoordinateByIdAndContainerType(gameData, reservedStorage.getContainerId(), reservedStorage.getContainerType());
    }

    private Coordinate getCoordinateByIdAndContainerType(GameData gameData, UUID containerId, ContainerType containerType) {
        return switch (containerType) {
            case STORAGE -> getCoordinateByBuildingModuleId(gameData, containerId);
            case SURFACE -> getCoordinateBySurfaceId(gameData, containerId);
            case CONSTRUCTION_AREA -> getCoordinateByConstructionAreaId(gameData, containerId);
            default -> throw ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, "Unhandled ContainerType: " + containerId);
        };
    }

    private Coordinate getCoordinateByBuildingModuleId(GameData gameData, UUID buildingModuleId) {
        UUID constructionAreaId = gameData.getBuildingModules()
            .findByIdValidated(buildingModuleId)
            .getConstructionAreaId();

        return getCoordinateByConstructionAreaId(gameData, constructionAreaId);
    }

    private Coordinate getCoordinateByConstructionAreaId(GameData gameData, UUID constructionAreaId) {
        UUID surfaceId = gameData.getConstructionAreas()
            .findByIdValidated(constructionAreaId)
            .getSurfaceId();

        return getCoordinateBySurfaceId(gameData, surfaceId);
    }

    private Coordinate getCoordinateBySurfaceId(GameData gameData, UUID surfaceId) {
        return gameData.getCoordinates()
            .findByReferenceIdValidated(surfaceId);
    }
}
