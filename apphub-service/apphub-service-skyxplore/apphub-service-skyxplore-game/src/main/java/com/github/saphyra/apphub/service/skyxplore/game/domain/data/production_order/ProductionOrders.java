package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class ProductionOrders extends Vector<ProductionOrder> {
    public List<ProductionOrder> getByProductionRequestIdAndConstructionAreaIdAndResourceDataId(UUID productionRequestId, UUID constructionAreaId, String resourceDataId) {
        return stream()
            .filter(productionOrder -> productionOrder.getProductionRequestId().equals(productionRequestId))
            .filter(productionOrder -> productionOrder.getConstructionAreaId().equals(constructionAreaId))
            .filter(productionOrder -> productionOrder.getResourceDataId().equals(resourceDataId))
            .collect(Collectors.toList());
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
