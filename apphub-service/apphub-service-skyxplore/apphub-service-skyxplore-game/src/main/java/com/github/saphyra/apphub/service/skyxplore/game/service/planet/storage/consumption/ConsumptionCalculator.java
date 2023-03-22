package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConsumptionCalculator {
    private final AllocatedResourceFactory allocatedResourceFactory;
    private final ReservedStorageFactory reservedStorageFactory;

    ConsumptionResult calculate(GameData gameData, UUID location, UUID externalReference, String dataId, Integer amount) {
        int storedAmount = gameData.getStoredResources().findByLocationAndDataId(location, dataId)
            .map(StoredResource::getAmount)
            .orElse(0);

        int allocatedAmount = gameData.getAllocatedResources()
            .getByLocationAndDataId(location, dataId)
            .stream()
            .mapToInt(AllocatedResource::getAmount)
            .sum();

        int availableAmount = storedAmount - allocatedAmount;
        int allocatableAmount = Math.min(amount, availableAmount);
        int reservedAmount = amount - allocatableAmount;

        return ConsumptionResult.builder()
            .allocation(allocatedResourceFactory.create(location, externalReference, dataId, allocatableAmount))
            .reservation(reservedStorageFactory.create(location, externalReference, dataId, reservedAmount))
            .build();
    }
}
