package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.building;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class AdjacentRandomEmptySurfaceProvider {
    private final AdjacentEmptySurfaceProvider adjacentEmptySurfaceProvider;

    Surface getRandomEmptySurfaceNextTo(List<Surface> surfacesWithMatchingType, Collection<Surface> surfaces) {
        for (Surface surface : surfacesWithMatchingType) {
            Optional<Surface> emptySurface = adjacentEmptySurfaceProvider.getEmptySurfaceNextTo(surface.getCoordinate(), surfaces);
            if (emptySurface.isPresent()) {
                return emptySurface.get();
            }
        }

        throw new IllegalStateException("No empty surface found next to the surfaces with matching type.");
    }
}
