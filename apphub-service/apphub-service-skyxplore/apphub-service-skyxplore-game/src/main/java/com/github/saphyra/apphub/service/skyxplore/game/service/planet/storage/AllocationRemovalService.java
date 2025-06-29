package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationRemovalService {
    private final StoredResourceConverter storedResourceConverter;

    public void removeAllocationsAndReservations(GameProgressDiff gameProgressDiff, GameData gameData, UUID externalReference) {
        log.info("Removing allocatedResources and ReservedStorages for externalReference {}", externalReference);
        gameData.getStoredResources()
            .getByAllocatedBy(externalReference)
            .forEach(storedResource -> {
                storedResource.setAllocatedBy(null);
                gameProgressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), storedResource));
            });

        ReservedStorages reservedStorages = gameData.getReservedStorages();
        reservedStorages.getByExternalReference(externalReference)
            .stream()
            .peek(reservedStorage -> log.info("Deleting {}", reservedStorage))
            .peek(rs -> gameProgressDiff.delete(rs.getReservedStorageId(), GameItemType.RESERVED_STORAGE))
            .forEach(reservedStorages::remove);
    }
}
