package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class HqDeconstructionPreconditions {
    private final HeadquartersUtil headquartersUtil;
    private final StorageCalculator storageCalculator;
    private final StoredResourceAmountQueryService storedResourceAmountQueryService;

    void checkHqCanBeDeconstructed(GameData gameData, Building building) {
        headquartersUtil.getStores()
            .forEach((storageType, capacity) -> checkStorageStatus(gameData, building.getLocation(), storageType, capacity * building.getLevel()));
    }

    private void checkStorageStatus(GameData gameData, UUID location, StorageType storageType, int capacity) {
        int storageCapacity = storageCalculator.calculateCapacity(gameData, location, storageType);
        int storedAmount = storedResourceAmountQueryService.getActualAmount(gameData, location, storageType);

        if (storageCapacity - capacity < storedAmount) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.SKYXPLORE_STORAGE_USED, "Cannot deconstruct " + GameConstants.DATA_ID_HEADQUARTERS + ", because it is still in use");
        }
    }
}
