package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ProduceResourcesService {
    private final ProductionOrderFactory productionOrderFactory;
    private final ProductionOrderProcessingService productionOrderProcessingService;
    private final TickCache tickCache;
    private final ProductionOrderToModelConverter productionOrderToModelConverter;

    public void produceResources(UUID gameId, Planet planet, Construction construction) {
        List<UUID> reservedStorageIds = planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(construction.getConstructionId()))
            .peek(reservedStorage -> log.debug("{} found for construction {} in game {}", reservedStorage, construction.getConstructionId(), gameId))
            .map(ReservedStorage::getReservedStorageId)
            .collect(Collectors.toList());
        log.debug("ReservedStorageIds for construction {}: {} in game {}", construction.getConstructionId(), reservedStorageIds, gameId);

        reservedStorageIds.stream()
            .map(reservedStorageId -> planet.getStorageDetails().getReservedStorages().findById(reservedStorageId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(reservedStorage -> placeAndProcessOrder(gameId, planet, reservedStorage));
    }

    private void placeAndProcessOrder(UUID gameId, Planet planet, ReservedStorage reservedStorage) {
        log.debug("Placing order for {}", reservedStorage);
        List<ProductionOrder> orders = planet.getOrders()
            .stream()
            .filter(productionOrder -> productionOrder.getExternalReference().equals(reservedStorage.getReservedStorageId()))
            .peek(productionOrder -> log.debug("{} found for {} in game {}", productionOrder, reservedStorage, gameId))
            .collect(Collectors.toList());

        //Creating order if not present for the given reservedStorage (Probably the processing has not started yet)
        if (orders.isEmpty()) {
            log.info("No ProductionOrder found for {} in game {}", reservedStorage, gameId);
            ProductionOrder newOrder = productionOrderFactory.create(reservedStorage.getReservedStorageId(), planet.getPlanetId(), LocationType.PLANET, reservedStorage.getDataId(), reservedStorage.getAmount());
            planet.getOrders().add(newOrder);
            orders.add(newOrder);
            tickCache.get(gameId)
                .getGameItemCache()
                .save(productionOrderToModelConverter.convert(newOrder, gameId));
        }

        orders.forEach(order -> productionOrderProcessingService.processOrder(gameId, planet, order));
    }
}
