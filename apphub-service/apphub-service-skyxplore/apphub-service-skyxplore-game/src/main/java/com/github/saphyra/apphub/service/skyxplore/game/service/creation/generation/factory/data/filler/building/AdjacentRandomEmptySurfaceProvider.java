package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
    private final RandomEmptySurfaceProvider randomEmptySurfaceProvider;

    Surface getRandomEmptySurfaceNextTo(List<Surface> surfacesWithMatchingType, Collection<Surface> surfaces, GameData gameData) {
        for (Surface surface : surfacesWithMatchingType) {
            Coordinate coordinate = gameData.getCoordinates()
                .findByReferenceIdValidated(surface.getSurfaceId());
            Optional<Surface> emptySurface = adjacentEmptySurfaceProvider.getEmptySurfaceNextTo(coordinate, surfaces, gameData);
            if (emptySurface.isPresent()) {
                return emptySurface.get();
            }
        }

        //If no empty surface left next to any surfaces, return a random empty surface
        return randomEmptySurfaceProvider.getEmptyDesertSurface(surfaces, gameData);
    }
}
