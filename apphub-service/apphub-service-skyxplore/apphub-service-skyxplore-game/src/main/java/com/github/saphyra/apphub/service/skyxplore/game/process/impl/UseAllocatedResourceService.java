package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UseAllocatedResourceService {
    public void resolveAllocations(SyncCache syncCache, GameData gameData, UUID location, UUID ownerId, UUID externalReference) {
        gameData.getReservedStorages()
            .getByExternalReference(externalReference)
            .forEach(rs -> resolveAllocation(syncCache, gameData, location, ownerId, externalReference, rs));
    }

    private void resolveAllocation(SyncCache syncCache, GameData gameData, UUID location, UUID ownerId, UUID externalReference, ReservedStorage reservedStorage) {
        log.info("Resolving allocation for {}", reservedStorage);

        AllocatedResource allocatedResource = gameData.getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(externalReference, reservedStorage.getDataId());
        log.info("Resolving {}", allocatedResource);
        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataIdOrDefault(location, reservedStorage.getDataId());
        storedResource.decreaseAmount(allocatedResource.getAmount());
        log.info("{} left.", storedResource);
        allocatedResource.setAmount(0);

        syncCache.allocatedResourceResolved(ownerId, location, allocatedResource, storedResource);
    }
}
