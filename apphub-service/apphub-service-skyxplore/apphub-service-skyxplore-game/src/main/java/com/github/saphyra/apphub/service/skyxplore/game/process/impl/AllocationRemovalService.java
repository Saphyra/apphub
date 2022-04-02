package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AllocationRemovalService {
    private final WsMessageSender messageSender;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    public void removeAllocationsAndReservations(SyncCache syncCache, Planet planet, UUID externalReference) {
        StorageDetails storageDetails = planet.getStorageDetails();

        AllocatedResources allocatedResources = storageDetails.getAllocatedResources();
        allocatedResources.getByExternalReference(externalReference)
            .stream()
            .peek(ar -> syncCache.deleteGameItem(ar.getAllocatedResourceId(), GameItemType.ALLOCATED_RESOURCE))
            .forEach(allocatedResources::remove);

        ReservedStorages reservedStorages = storageDetails.getReservedStorages();
        reservedStorages.getByExternalReference(externalReference)
            .stream()
            .peek(rs -> syncCache.deleteGameItem(rs.getReservedStorageId(), GameItemType.RESERVED_STORAGE))
            .forEach(reservedStorages::remove);

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
