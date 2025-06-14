package com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class Convoys extends Vector<Convoy> {
    public List<Convoy> getByResourceDeliveryRequestId(UUID resourceDeliveryRequestId) {
        return stream()
            .filter(convoy -> convoy.getResourceDeliveryRequestId().equals(resourceDeliveryRequestId))
            .toList();
    }

    public Convoy findByIdValidated(UUID convoyId) {
        return findById(convoyId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Convoy not found by id " + convoyId));
    }

    public Optional<Convoy> findById(UUID convoyId) {
        return stream()
            .filter(convoy -> convoy.getConvoyId().equals(convoyId))
            .findAny();
    }

    public void remove(UUID convoyId) {
        removeIf(convoy -> convoy.getConvoyId().equals(convoyId));
    }
}
