package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AvailableResourceCounter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SilentResourceAllocationService {
    private final AllocatedResourceFactory allocatedResourceFactory;
    private final AvailableResourceCounter availableResourceCounter;
    private final ReservedStorageFactory reservedStorageFactory;
    private final TickCache tickCache;
    private final ReservedStorageToModelConverter reservedStorageToModelConverter;
    private final AllocatedResourceToModelConverter allocatedResourceToModelConverter;
    private final WsMessageSender messageSender;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    /*
    Allocating free resources, and reserving the necessary storage for the production.
    Does not check if storage has enough place, to avoid deadlocks. (However full storage should block user interactions)
    Returns the newly created reservations, so the caller can create orders based on the missing resources
     */
    List<ReservedStorage> allocateResources(UUID gameId, Planet planet, UUID externalReference, Map<String, Integer> requiredResources) {
        return requiredResources.entrySet()
            .stream()
            .map(entry -> allocateResource(gameId, planet, externalReference, entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    private ReservedStorage allocateResource(UUID gameId, Planet planet, UUID externalReference, String dataId, Integer amount) {
        log.debug("Allocating {} amount of {} for externalReference {} in game {}", amount, dataId, externalReference, gameId);

        int availableAmount = availableResourceCounter.countAvailableAmount(planet.getStorageDetails(), dataId);
        int allocatedAmount = Math.min(amount, availableAmount);
        int reservedAmount = amount - allocatedAmount;

        AllocatedResource allocatedResource = allocatedResourceFactory.create(planet.getPlanetId(), LocationType.PRODUCTION, externalReference, dataId, allocatedAmount);
        ReservedStorage reservedStorage = reservedStorageFactory.create(planet.getPlanetId(), LocationType.PRODUCTION, externalReference, dataId, reservedAmount);
        log.debug("{} amount of {} is available in storage, so {} is allocated, and {} storage is reserved.", availableAmount, dataId, allocatedAmount, reservedAmount);

        planet.getStorageDetails()
            .getAllocatedResources()
            .add(allocatedResource);
        planet.getStorageDetails()
            .getReservedStorages()
            .add(reservedStorage);

        TickCacheItem tickCacheItem = tickCache.get(gameId);

        tickCacheItem.getGameItemCache()
            .saveAll(List.of(
                reservedStorageToModelConverter.convert(reservedStorage, gameId),
                allocatedResourceToModelConverter.convert(allocatedResource, gameId)
            ));

        tickCacheItem.getMessageCache()
            .add(
                planet.getOwner(),
                WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
                planet.getPlanetId(),
                () -> messageSender.planetStorageModified(
                    planet.getOwner(),
                    planet.getPlanetId(),
                    planetStorageOverviewQueryService.getStorage(planet)
                )
            );

        return reservedStorage;
    }
}
