package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
class RequiredEmptyStorageCalculator {
    private final ResourceDataService resourceDataService;

    int getRequiredStorageAmount(StorageType storageType, Map<String, ConsumptionResult> consumptions) {
        return consumptions.values()
            .stream()
            .map(ConsumptionResult::getReservation)
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType() == storageType)
            .mapToInt(value -> value.getAmount() * resourceDataService.get(value.getDataId()).getMass())
            .sum();
    }
}
