package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionOrderProcessingService {
    private final AssignBuildingToOrderService assignBuildingToOrderService;
    private final WorkOnOrderService workOnOrderService;

    void processOrder(UUID gameId, Planet planet, ProductionOrder order) {
        if (isNull(order.getAssignee())) {
            log.debug("{} is not assigned. Assigning to building in game {}", order, gameId);
            assignBuildingToOrderService.assignOrder(gameId, planet, order, this);
        }

        if (nonNull(order.getAssignee())) {
            log.debug("{} is assigned in game {}", order, gameId);
            workOnOrderService.workOnOrder(gameId, planet, order, this);
        }
    }
}
