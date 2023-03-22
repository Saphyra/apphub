package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AvailableResourceCounter {
    /*
    Calculating the not-assigned resource present in the storage
     */
    public int countAvailableAmount(GameData gameData, UUID location, String dataId) {
        int storedAmount = gameData.getStoredResources()
            .findByLocationAndDataId(location, dataId)
            .map(StoredResource::getAmount)
            .orElse(0);
        int allocatedAmount = gameData.getAllocatedResources()
            .getByLocationAndDataId(location, dataId)
            .stream()
            .mapToInt(AllocatedResource::getAmount)
            .sum();

        int result = storedAmount - allocatedAmount;
        log.debug("There are {} available amount of {}", allocatedAmount, dataId);
        return result;
    }
}
