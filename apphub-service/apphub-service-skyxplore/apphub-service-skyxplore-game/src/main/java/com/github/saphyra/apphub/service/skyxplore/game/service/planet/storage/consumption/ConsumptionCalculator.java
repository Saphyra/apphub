package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConsumptionCalculator {
    private final AllocatedResourceFactory allocatedResourceFactory;
    private final ReservedStorageFactory reservedStorageFactory;

    ConsumptionResult calculate(Planet planet, LocationType locationType, UUID externalReference, String dataId, Integer amount) {
        StorageDetails storageDetails = planet.getStorageDetails();
        int storedAmount = Optional.ofNullable(storageDetails.getStoredResources().get(dataId))
            .map(StoredResource::getAmount)
            .orElse(0);

        int allocatedAmount = storageDetails.getAllocatedResources()
            .stream()
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .mapToInt(AllocatedResource::getAmount)
            .sum();

        int availableAmount = storedAmount - allocatedAmount;
        int allocatableAmount = Math.min(amount, availableAmount);
        int reservedAmount = amount - allocatableAmount;

        return ConsumptionResult.builder()
            .allocation(allocatedResourceFactory.create(planet.getPlanetId(), locationType, externalReference, dataId, allocatableAmount))
            .reservation(reservedStorageFactory.create(planet.getPlanetId(), locationType, externalReference, dataId, reservedAmount))
            .build();
    }
}
