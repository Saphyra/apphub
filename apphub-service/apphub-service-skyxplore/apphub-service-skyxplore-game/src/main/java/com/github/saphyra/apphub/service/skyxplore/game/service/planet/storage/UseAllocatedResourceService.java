package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UseAllocatedResourceService {
    private final StoredResourceConverter storedResourceConverter;
    private final AllocatedResourceConverter allocatedResourceConverter;

    public void resolveAllocations(SyncCache syncCache, GameData gameData, UUID location, UUID externalReference) {
        gameData.getReservedStorages()
            .getByExternalReference(externalReference)
            .forEach(rs -> resolveAllocation(syncCache, gameData, location, externalReference, rs));
    }

    private void resolveAllocation(SyncCache syncCache, GameData gameData, UUID location, UUID externalReference, ReservedStorage reservedStorage) {
        log.info("Resolving allocation for {}", reservedStorage);

        AllocatedResource allocatedResource = gameData.getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(externalReference, reservedStorage.getDataId());
        log.info("Resolving {}", allocatedResource);
        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataIdOrDefault(location, reservedStorage.getDataId());
        storedResource.decreaseAmount(allocatedResource.getAmount());
        log.info("{} left.", storedResource);
        allocatedResource.setAmount(0);

        syncCache.saveGameItem(allocatedResourceConverter.toModel(gameData.getGameId(), allocatedResource));
        syncCache.saveGameItem(storedResourceConverter.toModel(gameData.getGameId(), storedResource));
    }
}
