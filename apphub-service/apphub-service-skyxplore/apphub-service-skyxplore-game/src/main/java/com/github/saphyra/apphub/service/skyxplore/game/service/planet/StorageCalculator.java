package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageCalculator {
    private final StorageBuildingService storageBuildingService;

    public int calculateCapacity(GameData gameData, UUID location, StorageType storageType) {
        StorageBuildingData storageBuildingData = storageBuildingService.findByStorageType(storageType);

        return gameData.getBuildings()
            .getByLocationAndDataId(location, storageBuildingData.getId())
            .stream()
            .mapToInt(value -> value.getLevel() * storageBuildingData.getCapacity())
            .sum();
    }
}
