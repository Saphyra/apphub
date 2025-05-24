package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
@Deprecated(forRemoval = true)
public class UseAllocatedResourceService {
    private final StoredResourceConverter storedResourceConverter;

    public void resolveAllocations(GameProgressDiff gameProgressDiff, GameData gameData, UUID location, UUID externalReference) {
        gameData.getReservedStorages()
            .getByExternalReference(externalReference)
            .forEach(rs -> resolveAllocation(gameProgressDiff, gameData, location, externalReference, rs));
    }

    private void resolveAllocation(GameProgressDiff gameProgressDiff, GameData gameData, UUID location, UUID externalReference, ReservedStorage reservedStorage) {
        log.info("Resolving allocation for {}", reservedStorage);

        AllocatedResource allocatedResource = gameData.getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(externalReference, reservedStorage.getDataId());
        log.info("Resolving {}", allocatedResource);
        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataIdValidated(location, reservedStorage.getDataId());
        storedResource.decreaseAmount(allocatedResource.getAmount());
        log.info("{} left.", storedResource);
        allocatedResource.setAmount(0);

        gameProgressDiff.save(allocatedResourceConverter.toModel(gameData.getGameId(), allocatedResource));
        gameProgressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), storedResource));
    }
}
