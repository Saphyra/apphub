package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ResourceAssembler {
    private final ProductionBuildingOrderProcessor productionBuildingOrderProcessor;

    void assembleResource(UUID gameId, Planet planet, ProductionOrder order) {
        Building building = planet.getBuildings()
            .stream()
            .filter(b -> b.getBuildingId().equals(order.getAssignee()))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Building not found with id " + order.getAssignee()));

        productionBuildingOrderProcessor.processOrderByAssignedBuilding(gameId, planet, building, order);
    }
}
