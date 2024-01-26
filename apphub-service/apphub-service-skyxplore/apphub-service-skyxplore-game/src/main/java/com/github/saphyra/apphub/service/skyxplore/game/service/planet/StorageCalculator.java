package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageCalculator {
    private final StorageBuildingService storageBuildingService;
    private final HeadquartersUtil headquartersUtil;

    public int calculateCapacity(GameData gameData, UUID location, StorageType storageType) {
        StorageBuildingData storageBuildingData = storageBuildingService.findByStorageType(storageType);
        int hqCapacityPerLevel = headquartersUtil.getStores()
            .get(storageType);

        int storageCapacity = getStorageCapacity(gameData, location, storageBuildingData);
        int hqCapacity = getHqCapacity(gameData, location, hqCapacityPerLevel);

        return storageCapacity + hqCapacity;
    }

    private static int getStorageCapacity(GameData gameData, UUID location, StorageBuildingData storageBuildingData) {
        return gameData.getBuildings()
            .getByLocationAndDataId(location, storageBuildingData.getId())
            .stream()
            .filter(building -> gameData.getDeconstructions().findByExternalReference(building.getBuildingId()).isEmpty())
            .mapToInt(building -> building.getLevel() * storageBuildingData.getCapacity())
            .sum();
    }

    private int getHqCapacity(GameData gameData, UUID location, int capacityPerLevel) {
        return gameData.getBuildings()
            .getByLocationAndDataId(location, GameConstants.DATA_ID_HEADQUARTERS)
            .stream()
            .filter(building -> gameData.getDeconstructions().findByExternalReference(building.getBuildingId()).isEmpty())
            .mapToInt(building -> building.getLevel() * capacityPerLevel)
            .sum();
    }
}
