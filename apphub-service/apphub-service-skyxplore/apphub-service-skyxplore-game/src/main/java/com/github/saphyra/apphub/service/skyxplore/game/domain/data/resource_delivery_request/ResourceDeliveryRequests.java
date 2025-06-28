package com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

public class ResourceDeliveryRequests extends Vector<ResourceDeliveryRequest> {
    public ResourceDeliveryRequest findByIdValidated(UUID resourceDeliveryRequestId) {
        return findById(resourceDeliveryRequestId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ResourceDeliveryRequest not found by id " + resourceDeliveryRequestId));
    }

    public Optional<ResourceDeliveryRequest> findById(UUID resourceDeliveryRequestId) {
        return stream()
            .filter(resourceDeliveryRequest -> resourceDeliveryRequest.getResourceDeliveryRequestId().equals(resourceDeliveryRequestId))
            .findAny();
    }

    public List<ResourceDeliveryRequest> getByReservedStorageId(UUID reservedStorageId) {
        return stream()
            .filter(resourceDeliveryRequest -> resourceDeliveryRequest.getReservedStorageId().equals(reservedStorageId))
            .toList();
    }
}
