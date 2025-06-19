package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class AdjacentEmptySurfaceProvider {
    private final DistanceCalculator distanceCalculator;

    Optional<Surface> getEmptySurfaceNextTo(Coordinate coordinate, Collection<Surface> surfaces, GameData gameData) {
        for (Surface surface : surfaces) {
            if (gameData.getConstructionAreas().findBySurfaceId(surface.getSurfaceId()).isEmpty() && isNextTo(coordinate, gameData.getCoordinates().findByReferenceIdValidated(surface.getSurfaceId()))) {
                return Optional.of(surface);
            }
        }
        return Optional.empty();
    }

    private boolean isNextTo(Coordinate c1, Coordinate c2) {
        return distanceCalculator.getDistance(c1, c2) == 1;
    }
}
