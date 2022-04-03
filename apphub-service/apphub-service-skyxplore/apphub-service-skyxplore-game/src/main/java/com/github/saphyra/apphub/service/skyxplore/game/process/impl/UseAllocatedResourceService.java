package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
//TODO unit test
public class UseAllocatedResourceService {
    private final AllocatedResourceToModelConverter allocatedResourceToModelConverter;
    private final StoredResourceToModelConverter storedResourceToModelConverter;
    private final WsMessageSender messageSender;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    //TODO extract
    public void resolveAllocations(SyncCache syncCache, UUID gameId, Planet planet, UUID externalReference) {
        planet.getStorageDetails()
            .getReservedStorages()
            .getByExternalReference(externalReference)
            .forEach(rs -> resolveAllocation(syncCache, gameId, planet, externalReference, rs));
    }

    private void resolveAllocation(SyncCache syncCache, UUID gameId, Planet planet, UUID externalReference, ReservedStorage reservedStorage) {
        log.info("Resolving allocation for {}", reservedStorage);

        StorageDetails storageDetails = planet.getStorageDetails();
        AllocatedResource allocatedResource = storageDetails.getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(externalReference, reservedStorage.getDataId());
        log.info("Resolving {}", allocatedResource);
        StoredResource storedResource = storageDetails.getStoredResources()
            .get(reservedStorage.getDataId());
        storedResource.decreaseAmount(allocatedResource.getAmount());
        log.info("{} left.", storedResource);
        allocatedResource.setAmount(0);

        syncCache.saveGameItem(allocatedResourceToModelConverter.convert(allocatedResource, gameId));
        syncCache.saveGameItem(storedResourceToModelConverter.convert(storedResource, gameId));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
            planet.getPlanetId(),
            () -> messageSender.planetStorageModified(
                planet.getOwner(),
                planet.getPlanetId(),
                planetStorageOverviewQueryService.getStorage(planet)
            )
        );
    }
}
