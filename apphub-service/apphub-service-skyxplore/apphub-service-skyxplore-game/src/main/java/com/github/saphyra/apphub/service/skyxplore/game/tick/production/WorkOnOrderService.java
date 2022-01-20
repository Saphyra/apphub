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
class WorkOnOrderService {
    private final ProcessProductionOrderRequirementsService processProductionOrderRequirementsService;
    private final ProductionOrderRequirementsMetCalculator productionOrderRequirementsMetCalculator;
    private final ResourceAssembler resourceAssembler;
    private final DeliverProductionOrderService deliverProductionOrderService;

    void workOnOrder(UUID gameId, Planet planet, ProductionOrder order, ProductionOrderProcessingService productionOrderProcessingService) {
        processProductionOrderRequirementsService.processRequirements(gameId, planet, order, productionOrderProcessingService);

        if (productionOrderRequirementsMetCalculator.areRequirementsMet(planet, order.getProductionOrderId())) {
            resourceAssembler.assembleResource(gameId, planet, order);
        }

        if (order.getCurrentWorkPoints() == order.getRequiredWorkPoints()) {
            deliverProductionOrderService.deliverOrder(gameId, planet, order);
        }
    }
}
