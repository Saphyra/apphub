package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionRequirementsAllocationService {
    private final AllocatedResourceFactory allocatedResourceFactory;
    private final ReservedStorageFactory reservedStorageFactory;
    private final AllocatedResourceConverter allocatedResourceConverter;
    private final ReservedStorageConverter reservedStorageConverter;
    private final StorageCapacityService storageCapacityService;

    /**
     * @return reservedStorageId
     */
    UUID allocate(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID externalReference, String dataId, Integer amount) {
        log.info("Allocating {} of {}", amount, dataId);
        int availableAmount = storageCapacityService.countAvailableResourceAmount(gameData, containerId, dataId);

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

        progressDiff.save(allocatedResourceConverter.toModel(gameData.getGameId(), allocatedResource));
        progressDiff.save(reservedStorageConverter.toModel(gameData.getGameId(), reservedStorage));

        return reservedStorage.getReservedStorageId();
    }
}
