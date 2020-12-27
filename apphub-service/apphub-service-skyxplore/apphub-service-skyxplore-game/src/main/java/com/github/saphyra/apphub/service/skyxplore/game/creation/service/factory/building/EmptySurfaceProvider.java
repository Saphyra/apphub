package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EmptySurfaceProvider {
    private final DistanceCalculator distanceCalculator;

    Surface getEmptySurfaceForType(SurfaceType surfaceType, Collection<Surface> surfaces) {
        List<Surface> surfacesWithMatchingType = surfaces.stream()
            .filter(surface -> surface.getSurfaceType().equals(surfaceType))
            .collect(Collectors.toList());

        if (surfacesWithMatchingType.isEmpty()) {
            log.debug("There is no surface with type {}", surfaceType);
            Surface randomEmptySurface = getRandomEmptySurface(surfaces);
            randomEmptySurface.setSurfaceType(surfaceType);
            return randomEmptySurface;
        }

        Optional<Surface> emptySurfaceWithMatchingType = surfacesWithMatchingType.stream()
            .filter(surface -> isNull(surface.getBuilding()))
            .findFirst();
        if (emptySurfaceWithMatchingType.isPresent()) {
            log.debug("Empty surface found for surfaceType {}: {}", surfaceType, emptySurfaceWithMatchingType);
            return emptySurfaceWithMatchingType.get();
        }

        Surface randomEmptySurfaceNextToType = getRandomEmptySurfaceNextTo(surfacesWithMatchingType, surfaces);
        randomEmptySurfaceNextToType.setSurfaceType(surfaceType);
        log.debug("Random empty surface next to surfaceType {}: {}", surfaceType, randomEmptySurfaceNextToType);
        return randomEmptySurfaceNextToType;
    }

    private Surface getRandomEmptySurface(Collection<Surface> surfaces) {
        return surfaces.stream()
            .filter(surface -> surface.getSurfaceType().equals(SurfaceType.DESERT))
            .filter(surface -> isNull(surface.getBuilding()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("There are no empty surfaces left."));
    }

    private Surface getRandomEmptySurfaceNextTo(List<Surface> surfacesWithMatchingType, Collection<Surface> surfaces) {
        for (Surface surface : surfacesWithMatchingType) {
            Optional<Surface> emptySurface = getEmptySurfaceNextTo(surface.getCoordinate(), surfaces);
            if (emptySurface.isPresent()) {
                return emptySurface.get();
            }
        }

        throw new IllegalStateException("No empty surface found next to the surfaces with matching type.");
    }

    private Optional<Surface> getEmptySurfaceNextTo(Coordinate coordinate, Collection<Surface> surfaces) {
        for (Surface surface : surfaces) {
            if (isNull(surface.getBuilding()) && isNextTo(coordinate, surface.getCoordinate())) {
                return Optional.of(surface);
            }
        }
        return Optional.empty();
    }

    private boolean isNextTo(Coordinate c1, Coordinate c2) {
        return distanceCalculator.getDistance(c1, c2) == 1;
    }
}
