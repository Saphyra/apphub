package com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

public class Surfaces extends Vector<Surface> {
    public Collection<Surface> getByPlanetId(UUID planetId) {
        return stream()
            .filter(surface -> surface.getPlanetId().equals(planetId))
            .toList();
    }

    public Surface findBySurfaceId(UUID surfaceId) {
        return stream()
            .filter(surface -> surface.getSurfaceId().equals(surfaceId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Surface not found by surfaceId " + surfaceId));
    }
}
