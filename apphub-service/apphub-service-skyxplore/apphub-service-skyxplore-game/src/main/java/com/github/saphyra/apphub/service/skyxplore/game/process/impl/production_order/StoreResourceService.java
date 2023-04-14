package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class StoreResourceService {
    private final AllocatedResourceConverter allocatedResourceConverter;
    private final StoredResourceFactory storedResourceFactory;

    void storeResource(SyncCache syncCache, GameData gameData, UUID location, UUID ownerId, ReservedStorage reservedStorage, AllocatedResource allocatedResource, int amount) {
        log.info("ProductionOrder finished. Storing {} of {}", amount, reservedStorage.getDataId());

        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataId(location, reservedStorage.getDataId())
            .orElseGet(() -> storedResourceFactory.create(location, reservedStorage.getDataId()));

        log.info("Before update: {}, {}, {}", storedResource, allocatedResource, reservedStorage);

        if (nonNull(allocatedResource)) {
            allocatedResource.increaseAmount(amount);
            syncCache.saveGameItem(allocatedResourceConverter.toModel(gameData.getGameId(), allocatedResource));
        }
        storedResource.increaseAmount(amount);
        reservedStorage.decreaseAmount(amount);

        log.info("After update: {}, {}, {}", storedResource, allocatedResource, reservedStorage);

        syncCache.resourceStored(ownerId, location, storedResource, reservedStorage);
    }
}
