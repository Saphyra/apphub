package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

@Deprecated(forRemoval = true)
public class Buildings extends Vector<Building> {
    public Optional<Building> findBySurfaceId(UUID surfaceId) {
        return stream()
            .filter(building -> building.getSurfaceId().equals(surfaceId))
            .findFirst();
    }

    public List<Building> getByLocationAndDataId(UUID location, String dataId) {
        return stream()
            .filter(building -> building.getLocation().equals(location))
            .filter(building -> building.getDataId().equals(dataId))
            .toList();
    }

    public List<Building> getByLocation(UUID location) {
        return stream()
            .filter(building -> building.getLocation().equals(location))
            .toList();
    }

    public Building findByBuildingId(UUID buildingId) {
        return stream()
            .filter(building -> building.getBuildingId().equals(buildingId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Building not found by id " + buildingId));
    }
}
