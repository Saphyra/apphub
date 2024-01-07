package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationRemovalService {
    public void removeAllocationsAndReservations(SyncCache syncCache, GameData gameData, UUID externalReference) {
        log.info("Removing allocatedResources and ReservedStorages for externalReference {}", externalReference);
        AllocatedResources allocatedResources = gameData.getAllocatedResources();
        allocatedResources.getByExternalReference(externalReference)
            .stream()
            .peek(allocatedResource -> log.info("Deleting {}", allocatedResource))
            .peek(ar -> syncCache.deleteGameItem(ar.getAllocatedResourceId(), GameItemType.ALLOCATED_RESOURCE))
            .forEach(allocatedResources::remove);

        ReservedStorages reservedStorages = gameData.getReservedStorages();
        reservedStorages.getByExternalReference(externalReference)
            .stream()
            .peek(reservedStorage -> log.info("Deleting {}", reservedStorage))
            .peek(rs -> syncCache.deleteGameItem(rs.getReservedStorageId(), GameItemType.RESERVED_STORAGE))
            .forEach(reservedStorages::remove);
    }
}
