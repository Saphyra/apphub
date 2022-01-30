package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocatedResourceResolver {
    private final StoredResourceToModelConverter storedResourceToModelConverter;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;
    private final WsMessageSender messageSender;
    private final TickCache tickCache;

    /*
    Removing reservedStorages belong to the order
    Removing allocatedResources belong to the order
    Removing resource from the storage allocated for the order
     */
    public void resolveAllocations(UUID gameId, Planet planet, UUID externalReference) {
        log.debug("Resolving allocations for externalReference {} in game {}. {}", externalReference, gameId, planet.getStorageDetails());
        List<UUID> reservedStorageIds = planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference))
            .peek(reservedStorage -> tickCache.get(gameId).getGameItemCache().delete(reservedStorage.getReservedStorageId(), GameItemType.RESERVED_STORAGE))
            .map(ReservedStorage::getReservedStorageId)
            .collect(Collectors.toList());
        log.debug("ReservedStorageIds found for externalReference {}: {} in game {}", externalReference, reservedStorageIds, gameId);
        reservedStorageIds.forEach(uuid -> planet.getStorageDetails()
            .getReservedStorages()
            .removeIf(reservedStorage -> reservedStorageIds.contains(reservedStorage.getReservedStorageId()))
        );

        List<UUID> allocatedResourceIds = planet.getStorageDetails()
            .getAllocatedResources()
            .stream()
            .filter(allocatedResource -> allocatedResource.getExternalReference().equals(externalReference))
            .peek(allocatedResource -> resolveAllocation(gameId, planet, allocatedResource))
            .map(AllocatedResource::getAllocatedResourceId)
            .collect(Collectors.toList());
        planet.getStorageDetails()
            .getAllocatedResources()
            .removeIf(allocatedResource -> allocatedResourceIds.contains(allocatedResource.getAllocatedResourceId()));

        tickCache.get(gameId)
            .getMessageCache()
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
    }

    private void resolveAllocation(UUID gameId, Planet planet, AllocatedResource allocatedResource) {
        log.debug("Resolving {} in game {}", allocatedResource, gameId);
        StoredResource storedResource = planet.getStorageDetails()
            .getStoredResources()
            .get(allocatedResource.getDataId());
        if (isNull(storedResource)) {
            throw new NullPointerException("StoredResource not found for dataId " + allocatedResource.getDataId());
        }
        storedResource.reduceAmount(allocatedResource.getAmount());
        tickCache.get(gameId)
            .getGameItemCache()
            .save(storedResourceToModelConverter.convert(storedResource, gameId));

        tickCache.get(gameId)
            .getGameItemCache()
            .delete(allocatedResource.getAllocatedResourceId(), GameItemType.ALLOCATED_RESOURCE);
    }
}
