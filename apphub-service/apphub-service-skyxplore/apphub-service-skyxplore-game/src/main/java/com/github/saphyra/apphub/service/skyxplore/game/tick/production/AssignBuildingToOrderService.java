package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class AssignBuildingToOrderService {
    private final ProductionOrderFactory productionOrderFactory;
    private final SilentResourceAllocationService silentResourceAllocationService;
    private final ProductionBuildingService productionBuildingService;
    private final TickCache tickCache;
    private final ProductionOrderToModelConverter productionOrderToModelConverter;
    private final ProductionBuildingFinder productionBuildingFinder;

    /*
    Assigning order to the best available productionBuilding. Splitting up the order to smaller parts if it is too big.
    Calculating resource requirements, and assigning resources and storage for the production.
     */
    void assignOrder(UUID gameId, Planet planet, ProductionOrder order, ProductionOrderProcessingService productionOrderProcessingService) {
        Optional<Building> maybeBuilding = productionBuildingFinder.findProducer(planet, order.getDataId());
        log.debug("Assigning {} to {} in game {}", order, maybeBuilding, gameId);
        if (maybeBuilding.isPresent()) {
            Building building = maybeBuilding.get();
            ProductionBuilding productionBuilding = productionBuildingService.get(building.getDataId());
            ProductionData productionData = productionBuilding.getGives()
                .get(order.getDataId());
            int originallyRequestedAmount = order.getAmount();
            int batchSize = Math.min(productionData.getMaxBatchSize(), originallyRequestedAmount);
            log.debug("BatchSize for {}: {} in game {}", order, batchSize, gameId);

            order.setRequiredWorkPoints(productionData.getConstructionRequirements().getRequiredWorkPoints() * batchSize);
            order.setAssignee(building.getBuildingId());
            order.setAmount(batchSize);
            GameItemCache gameItemCache = tickCache.get(gameId)
                .getGameItemCache();
            gameItemCache.save(productionOrderToModelConverter.convert(order, gameId));

            Map<String, Integer> requiredResources = productionData.getConstructionRequirements()
                .getRequiredResources()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue() * batchSize));
            log.debug("Required resources for {}: {} in game {}", order, requiredResources, gameId);

            silentResourceAllocationService.allocateResources(gameId, planet, order.getProductionOrderId(), requiredResources)
                .stream()
                .map(reservedStorage -> productionOrderFactory.create(
                    reservedStorage.getReservedStorageId(),
                    planet.getPlanetId(),
                    LocationType.PLANET,
                    reservedStorage.getDataId(),
                    reservedStorage.getAmount()
                ))
                .peek(productionOrder -> planet.getOrders().add(productionOrder))
                .peek(productionOrder -> gameItemCache.save(productionOrderToModelConverter.convert(productionOrder, gameId)))
                .forEach(productionOrder -> productionOrderProcessingService.processOrder(gameId, planet, productionOrder));

            if (originallyRequestedAmount > batchSize) {
                log.debug("Required amount {} is greater than max batchSize {} in game {}", originallyRequestedAmount, batchSize, gameId);
                int remainingAmount = order.getAmount() - batchSize;
                ProductionOrder newOrder = productionOrderFactory.create(order.getExternalReference(), planet.getPlanetId(), LocationType.PLANET, order.getDataId(), remainingAmount);
                planet.getOrders().add(newOrder);
                productionOrderProcessingService.processOrder(gameId, planet, newOrder);
                gameItemCache.save(productionOrderToModelConverter.convert(newOrder, gameId));
            }
        }
    }
}
