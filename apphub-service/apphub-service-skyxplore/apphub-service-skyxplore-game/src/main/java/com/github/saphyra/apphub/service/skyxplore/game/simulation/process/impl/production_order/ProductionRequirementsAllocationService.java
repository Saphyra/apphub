package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AvailableResourceCounter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionRequirementsAllocationService {
    private final AvailableResourceCounter availableResourceCounter;
    private final AllocatedResourceFactory allocatedResourceFactory;
    private final ReservedStorageFactory reservedStorageFactory;

    /**
     * @return reservedStorageId
     */
    UUID allocate(SyncCache syncCache, GameData gameData, UUID location, UUID ownerId, UUID externalReference, String dataId, Integer amount) {
        log.info("Allocating {} of {}", amount, dataId);
        int availableAmount = availableResourceCounter.countAvailableAmount(gameData, location, dataId);

        int allocatedAmount = Math.min(amount, availableAmount);
        int reservedAmount = amount - allocatedAmount;

        log.info("Available: {}, Allocated: {}, Reserved: {}", availableAmount, allocatedAmount, reservedAmount);

        AllocatedResource allocatedResource = allocatedResourceFactory.create(location, externalReference, dataId, allocatedAmount);
        log.info("{} created.", allocatedResource);
        ReservedStorage reservedStorage = reservedStorageFactory.create(location, externalReference, dataId, reservedAmount);
        log.info("{} created.", reservedStorage);

        gameData.getAllocatedResources()
            .add(allocatedResource);

        gameData.getReservedStorages()
            .add(reservedStorage);

        syncCache.resourceAllocated(ownerId, location, allocatedResource, reservedStorage);

        return reservedStorage.getReservedStorageId();
    }
}
