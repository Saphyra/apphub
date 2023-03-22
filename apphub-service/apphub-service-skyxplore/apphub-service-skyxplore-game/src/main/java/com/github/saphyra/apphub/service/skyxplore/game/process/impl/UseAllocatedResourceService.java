package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UseAllocatedResourceService {
    private final AllocatedResourceToModelConverter allocatedResourceToModelConverter;
    private final StoredResourceToModelConverter storedResourceToModelConverter;
    private final WsMessageSender messageSender;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    public void resolveAllocations(SyncCache syncCache, UUID gameId, GameData gameData, UUID location, UUID ownerId, UUID externalReference) {
        gameData.getReservedStorages()
            .getByExternalReference(externalReference)
            .forEach(rs -> resolveAllocation(syncCache, gameId, gameData, location, ownerId, externalReference, rs));
    }

    private void resolveAllocation(SyncCache syncCache, UUID gameId, GameData gameData, UUID location, UUID ownerId, UUID externalReference, ReservedStorage reservedStorage) {
        log.info("Resolving allocation for {}", reservedStorage);

        AllocatedResource allocatedResource = gameData.getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(externalReference, reservedStorage.getDataId());
        log.info("Resolving {}", allocatedResource);
        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataIdOrDefault(location, reservedStorage.getDataId());
        storedResource.decreaseAmount(allocatedResource.getAmount());
        log.info("{} left.", storedResource);
        allocatedResource.setAmount(0);

        syncCache.saveGameItem(allocatedResourceToModelConverter.convert(gameId, allocatedResource));
        syncCache.saveGameItem(storedResourceToModelConverter.convert(gameId, storedResource));

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
            location,
            () -> messageSender.planetStorageModified(
                ownerId,
                location,
                planetStorageOverviewQueryService.getStorage(gameData, location)
            )
        );
    }
}
