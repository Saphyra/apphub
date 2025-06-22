package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

public class Coordinates extends Vector<ReferredCoordinate> {
    public synchronized Coordinate findByReferenceIdValidated(UUID referenceId) {
        return findByReferenceId(referenceId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Coordinate not found by referenceId " + referenceId));
    }

    public synchronized Optional<Coordinate> findByReferenceId(UUID referenceId) {
        return stream()
            .filter(referredCoordinate -> referredCoordinate.getReferenceId().equals(referenceId))
            .findFirst()
            .map(ReferredCoordinate::getCoordinate);
    }

    public synchronized List<ReferredCoordinate> getByReferenceId(UUID referenceId) {
        return stream()
            .filter(referredCoordinate -> referredCoordinate.getReferenceId().equals(referenceId))
            .toList();
    }
}
