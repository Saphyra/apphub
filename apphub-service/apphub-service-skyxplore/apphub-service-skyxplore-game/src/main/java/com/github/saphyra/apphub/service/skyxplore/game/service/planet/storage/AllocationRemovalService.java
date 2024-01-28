package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationRemovalService {
    public void removeAllocationsAndReservations(GameProgressDiff gameProgressDiff, GameData gameData, UUID externalReference) {
        log.info("Removing allocatedResources and ReservedStorages for externalReference {}", externalReference);
        AllocatedResources allocatedResources = gameData.getAllocatedResources();
        allocatedResources.getByExternalReference(externalReference)
            .stream()
            .peek(allocatedResource -> log.info("Deleting {}", allocatedResource))
            .peek(ar -> gameProgressDiff.delete(ar.getAllocatedResourceId(), GameItemType.ALLOCATED_RESOURCE))
            .forEach(allocatedResources::remove);

        ReservedStorages reservedStorages = gameData.getReservedStorages();
        reservedStorages.getByExternalReference(externalReference)
            .stream()
            .peek(reservedStorage -> log.info("Deleting {}", reservedStorage))
            .peek(rs -> gameProgressDiff.delete(rs.getReservedStorageId(), GameItemType.RESERVED_STORAGE))
            .forEach(reservedStorages::remove);
    }
}
