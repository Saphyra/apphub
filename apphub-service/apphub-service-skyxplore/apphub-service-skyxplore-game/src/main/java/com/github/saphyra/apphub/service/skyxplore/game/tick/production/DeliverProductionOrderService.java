package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class DeliverProductionOrderService {
    private final TickCache tickCache;
    private final StoredResourceFactory storedResourceFactory;
    private final StoredResourceToModelConverter storedResourceToModelConverter;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;
    private final WsMessageSender messageSender;
    private final NewlyProducedResourceAllocationService newlyProducedResourceAllocationService;

    /*
    Putting the newly produced resource to the storage, and assigning it if there is reservedStorage.
     */
    void deliverOrder(UUID gameId, Planet planet, ProductionOrder order) {
        StoredResource storedResource = planet.getStorageDetails()
            .getStoredResources()
            .getOrDefault(order.getDataId(), storedResourceFactory.create(planet.getPlanetId(), LocationType.PLANET, order.getDataId(), 0))
            .increaseAmount(order.getAmount());

        TickCacheItem tickCacheItem = tickCache.get(gameId);
        GameItemCache gameItemCache = tickCacheItem.getGameItemCache();

        gameItemCache.save(storedResourceToModelConverter.convert(storedResource, gameId));

        Optional<ReservedStorage> maybeReservedStorage = planet.getStorageDetails()
            .getReservedStorages()
            .findById(order.getExternalReference());
        maybeReservedStorage.ifPresent(reservedStorage -> newlyProducedResourceAllocationService.allocateNewlyProducedResource(gameId, planet, order, reservedStorage));

        planet.getOrders().remove(order);
        gameItemCache.delete(order.getProductionOrderId(), GameItemType.PRODUCTION_ORDER);

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
    }
}
