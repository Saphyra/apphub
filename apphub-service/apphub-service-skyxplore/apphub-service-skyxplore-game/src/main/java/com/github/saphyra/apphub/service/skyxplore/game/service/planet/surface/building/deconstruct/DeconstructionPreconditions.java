package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceAmountQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionPreconditions {
    private final StorageBuildingService storageBuildingService;
    private final StorageCalculator storageCalculator;
    private final StoredResourceAmountQueryService storedResourceAmountQueryService;
    private final HqDeconstructionPreconditions hqDeconstructionPreconditions;

    void checkIfBuildingCanBeDeconstructed(GameData gameData, Building building) {
        StorageBuildingData buildingData = storageBuildingService.get(building.getDataId());
        if (isNull(buildingData)) {
            if(GameConstants.DATA_ID_HEADQUARTERS.equals(building.getDataId())){
                hqDeconstructionPreconditions.checkHqCanBeDeconstructed(gameData, building);
            }
            return;
        }

        int storageCapacity = storageCalculator.calculateCapacity(gameData, building.getLocation(), buildingData.getStores());
        int buildingCapacity = buildingData.getCapacity() * building.getLevel();
        int storedAmount = storedResourceAmountQueryService.getActualAmount(gameData, building.getLocation(), buildingData.getStores());

        log.info("Current capacity: {}, Capacity of building: {}, Stored amount: {}", storageCapacity, buildingCapacity, storedAmount);

        if (storageCapacity - buildingCapacity < storedAmount) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.SKYXPLORE_STORAGE_USED, "Cannot deconstruct " + building.getDataId() + ", because it is still in use");
        }
    }
}
