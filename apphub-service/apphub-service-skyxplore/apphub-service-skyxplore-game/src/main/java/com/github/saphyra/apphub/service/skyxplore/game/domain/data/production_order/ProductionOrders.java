package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class ProductionOrders extends Vector<ProductionOrder> {
    public Optional<ProductionOrder> findByProductionRequestIdAndConstructionAreaIdAndResourceDataId(UUID productionRequestId, UUID constructionAreaId, String resourceDataId) {
        return stream()
            .filter(productionOrder -> productionOrder.getProductionRequestId().equals(productionRequestId))
            .filter(productionOrder -> productionOrder.getConstructionAreaId().equals(constructionAreaId))
            .filter(productionOrder -> productionOrder.getResourceDataId().equals(resourceDataId))
            .findAny();
    }

    public ProductionOrder findByIdValidated(UUID productionOrderId) {
        return findById(productionOrderId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ProductionOrder not found by id " + productionOrderId));
    }

    public Optional<ProductionOrder> findById(UUID productionOrderId) {
        return stream()
            .filter(productionOrder -> productionOrder.getProductionOrderId().equals(productionOrderId))
            .findAny();
    }
}
