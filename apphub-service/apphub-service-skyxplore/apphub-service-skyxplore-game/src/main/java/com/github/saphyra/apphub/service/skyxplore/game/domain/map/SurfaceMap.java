package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor
public class SurfaceMap extends HashMap<Coordinate, Surface> {
    public SurfaceMap(Map<Coordinate, Surface> surfaceMap) {
        putAll(surfaceMap);
    }

    public Surface findByIdValidated(UUID surfaceId) {
        return findById(surfaceId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Surface not found with id " + surfaceId));
    }

    private Optional<Surface> findById(UUID surfaceId) {
        return values()
            .stream()
            .filter(surface -> surface.getSurfaceId().equals(surfaceId))
            .findFirst();
    }

    public Surface findByBuildingIdValidated(UUID buildingId) {
        return findByBuildingId(buildingId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Surface not found by buildingId " + buildingId));
    }

    private Optional<Surface> findByBuildingId(UUID buildingId) {
        return values()
            .stream()
            .filter(surface -> nonNull(surface.getBuilding()) && surface.getBuilding().getBuildingId().equals(buildingId))
            .findFirst();
    }
}
