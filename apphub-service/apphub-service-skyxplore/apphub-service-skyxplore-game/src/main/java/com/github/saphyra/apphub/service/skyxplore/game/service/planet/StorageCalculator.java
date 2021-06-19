package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageCalculator {
    private final StorageBuildingService storageBuildingService;

    public int calculateCapacity(Planet planet, StorageType storageType) {
        StorageBuilding storageBuilding = storageBuildingService.findByStorageType(storageType);

        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> building.getDataId().equals(storageBuilding.getId()))
            .mapToInt(value -> value.getLevel() * storageBuilding.getCapacity())
            .sum();
    }
}
