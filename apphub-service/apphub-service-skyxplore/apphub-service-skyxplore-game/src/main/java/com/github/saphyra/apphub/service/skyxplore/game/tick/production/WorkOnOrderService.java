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
class WorkOnOrderService {
    private final ProcessProductionOrderRequirementsService processProductionOrderRequirementsService;
    private final ProductionOrderRequirementsMetCalculator productionOrderRequirementsMetCalculator;
    private final ResourceAssembler resourceAssembler;
    private final DeliverProductionOrderService deliverProductionOrderService;

    void workOnOrder(UUID gameId, Planet planet, ProductionOrder order, ProductionOrderProcessingService productionOrderProcessingService) {
        log.debug("Working on {} in game {}", order, gameId);
        processProductionOrderRequirementsService.processRequirements(gameId, planet, order, productionOrderProcessingService);

        if (productionOrderRequirementsMetCalculator.areRequirementsMet(planet, order.getProductionOrderId())) {
            log.debug("Requirements are met for {} in game {}", order, gameId);
            resourceAssembler.assembleResource(gameId, planet, order);
        }

        if (order.getCurrentWorkPoints() == order.getRequiredWorkPoints()) {
            log.debug("{} is completed in game {}", order, gameId);
            deliverProductionOrderService.deliverOrder(gameId, planet, order);
        }
    }
}
