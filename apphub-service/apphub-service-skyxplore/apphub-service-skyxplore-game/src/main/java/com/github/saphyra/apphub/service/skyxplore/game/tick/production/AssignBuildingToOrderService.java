package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
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
//TODO unit test
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
        if (maybeBuilding.isPresent()) {
            Building building = maybeBuilding.get();
            ProductionBuilding productionBuilding = productionBuildingService.get(building.getDataId());
            ProductionData productionData = productionBuilding.getGives()
                .get(order.getDataId());
            int batchSize = Math.min(productionData.getMaxBatchSize(), order.getAmount());

            order.setRequiredWorkPoints(productionData.getConstructionRequirements().getRequiredWorkPoints() * batchSize);
            order.setAssignee(building.getBuildingId());
            order.setAmount(batchSize);
            tickCache.get(gameId)
                .getGameItemCache()
                .save(productionOrderToModelConverter.convert(order, gameId));

            Map<String, Integer> requiredResources = productionData.getConstructionRequirements()
                .getRequiredResources()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue() * batchSize));

            silentResourceAllocationService.allocateResources(gameId, planet, order.getProductionOrderId(), requiredResources)
                .stream()
                .map(reservedStorage -> productionOrderFactory.create(reservedStorage.getReservedStorageId(), planet.getPlanetId(), LocationType.PLANET, reservedStorage.getDataId(), reservedStorage.getAmount()))
                .peek(productionOrder -> planet.getOrders().add(productionOrder))
                .peek(productionOrder -> tickCache.get(gameId)
                    .getGameItemCache()
                    .save(productionOrderToModelConverter.convert(productionOrder, gameId))
                )
                .forEach(productionOrder -> productionOrderProcessingService.processOrder(gameId, planet, productionOrder));

            if (order.getAmount() > batchSize) {
                ProductionOrder newOrder = productionOrderFactory.create(order.getExternalReference(), planet.getPlanetId(), LocationType.PLANET, order.getDataId(), order.getAmount() - batchSize);
                planet.getOrders().add(newOrder);
                productionOrderProcessingService.processOrder(gameId, planet, newOrder);
                tickCache.get(gameId)
                    .getGameItemCache()
                    .save(productionOrderToModelConverter.convert(newOrder, gameId));
            }
        }
    }
}
