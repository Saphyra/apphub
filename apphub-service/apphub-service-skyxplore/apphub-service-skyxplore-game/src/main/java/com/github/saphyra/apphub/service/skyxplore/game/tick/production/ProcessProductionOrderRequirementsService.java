package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProcessProductionOrderRequirementsService {
    /*
    Working on the order's requirements
     */
    void processRequirements(UUID gameId, Planet planet, ProductionOrder order, ProductionOrderProcessingService productionOrderProcessingService) {
        log.debug("Processing requirements of {}", order);
        planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(order.getProductionOrderId()))
            .filter(reservedStorage -> reservedStorage.getAmount() > 0)
            .peek(reservedStorage -> log.debug("{} found for {} in game {}", reservedStorage, order, gameId))
            .map(reservedStorage -> planet.getOrders()
                .stream()
                .filter(productionOrder -> productionOrder.getExternalReference().equals(reservedStorage.getReservedStorageId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found for ReservedStorage " + reservedStorage.getReservedStorageId()))
            )
            .peek(productionOrder -> log.debug("{} has to be completed before working on {} in game {}", productionOrder, order, gameId))
            .forEach(productionOrder -> productionOrderProcessingService.processOrder(gameId, planet, productionOrder));
    }
}
