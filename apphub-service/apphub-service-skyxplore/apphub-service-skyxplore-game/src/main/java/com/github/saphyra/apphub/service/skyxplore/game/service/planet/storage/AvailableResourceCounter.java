package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AvailableResourceCounter {
    /*
    Calculating the not-assigned resource present in the storage
     */
    public int countAvailableAmount(StorageDetails storageDetails, String dataId) {
        int storedAmount = storageDetails.getStoredResources().get(dataId)
            .getAmount();
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
