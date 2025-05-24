package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class RequiredEmptyStorageCalculator {
    private final ResourceDataService resourceDataService;

    int getRequiredStorageAmount(StorageType storageType, Map<String, ConsumptionResult> consumptions) {
        return consumptions.values()
            .stream()
            .map(ConsumptionResult::getReservation)
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType() == storageType)
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }
}
