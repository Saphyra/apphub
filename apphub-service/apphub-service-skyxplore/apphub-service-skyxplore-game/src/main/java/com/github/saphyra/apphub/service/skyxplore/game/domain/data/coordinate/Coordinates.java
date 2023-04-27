package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.Vector;

public class Coordinates extends Vector<ReferredCoordinate> {
    public Coordinate findByReferenceId(UUID referenceId) {
        return stream()
            .filter(referredCoordinate -> referredCoordinate.getReferenceId().equals(referenceId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Coordinate not found by referenceId " + referenceId))
            .getCoordinate();
    }
}
