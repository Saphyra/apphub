package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AvailableResourceCounter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionRequirementsAllocationService {
    private final AvailableResourceCounter availableResourceCounter;
    private final AllocatedResourceFactory allocatedResourceFactory;
    private final ReservedStorageFactory reservedStorageFactory;
    private final AllocatedResourceToModelConverter allocatedResourceToModelConverter;
    private final ReservedStorageToModelConverter reservedStorageToModelConverter;
    private final WsMessageSender messageSender;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    UUID allocate(SyncCache syncCache, UUID gameId, Planet planet, UUID externalReference, String dataId, Integer amount) {
        StorageDetails storageDetails = planet.getStorageDetails();
        int availableAmount = availableResourceCounter.countAvailableAmount(storageDetails, dataId);

        int allocatedAmount = Math.max(amount, availableAmount);
        int reservedAmount = amount - allocatedAmount;

        AllocatedResource allocatedResource = allocatedResourceFactory.create(planet.getPlanetId(), LocationType.PRODUCTION, externalReference, dataId, allocatedAmount);
        ReservedStorage reservedStorage = reservedStorageFactory.create(planet.getPlanetId(), LocationType.PRODUCTION, externalReference, dataId, reservedAmount);

        storageDetails.getAllocatedResources()
            .add(allocatedResource);
        storageDetails.getReservedStorages()
            .add(reservedStorage);

        syncCache.saveGameItem(allocatedResourceToModelConverter.convert(allocatedResource, gameId));
        syncCache.saveGameItem(reservedStorageToModelConverter.convert(reservedStorage, gameId));
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

        return reservedStorage.getReservedStorageId();
    }
}
