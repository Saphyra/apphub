package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class AvailableResourceCounter {
    /*
    Calculating the not-assigned resource present in the storage
     */
    int countAvailableAmount(StorageDetails storageDetails, String dataId) {
        int storedAmount = Optional.ofNullable(storageDetails.getStoredResources().get(dataId))
            .map(StoredResource::getAmount)
            .orElse(0);
        int allocatedAmount = storageDetails.getAllocatedResources()
            .stream()
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .mapToInt(AllocatedResource::getAmount)
            .sum();

        int result = storedAmount - allocatedAmount;
        log.debug("There are {} available amount of {}", allocatedAmount, dataId);
        return result;
    }
}