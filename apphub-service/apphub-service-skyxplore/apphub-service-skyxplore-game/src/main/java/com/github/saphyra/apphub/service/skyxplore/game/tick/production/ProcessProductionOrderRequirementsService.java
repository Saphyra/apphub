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
        planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(productionOrder -> productionOrder.getExternalReference().equals(order.getProductionOrderId()))
            .map(reservedStorage -> planet.getOrders()
                .stream()
                .filter(productionOrder -> productionOrder.getExternalReference().equals(reservedStorage.getReservedStorageId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found for ReservedStorage " + reservedStorage.getReservedStorageId()))
            )
            .forEach(productionOrder -> productionOrderProcessingService.processOrder(gameId, planet, productionOrder));
    }
}
