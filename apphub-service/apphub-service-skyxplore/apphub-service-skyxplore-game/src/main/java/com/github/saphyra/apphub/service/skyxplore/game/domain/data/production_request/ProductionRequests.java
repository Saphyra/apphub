package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class ProductionRequests extends Vector<ProductionRequest> {
    public List<ProductionRequest> getByReservedStorageId(UUID reservedStorageId) {
        return stream()
            .filter(productionRequest -> productionRequest.getReservedStorageId().equals(reservedStorageId))
            .toList();
    }

    public ProductionRequest findByIdValidated(UUID productionRequestId) {
        return stream()
            .filter(productionRequest -> productionRequest.getProductionRequestId().equals(productionRequestId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ProductionRequest not found by id " + productionRequestId));
    }

    public void remove(UUID productionRequestId){
        removeIf(productionRequest -> productionRequest.getProductionRequestId().equals(productionRequestId));
    }
}
