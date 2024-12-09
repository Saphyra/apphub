package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
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
class EmptySurfaceProvider {
    private final MatchingSurfaceTypeFilter matchingSurfaceTypeFilter;
    private final RandomEmptySurfaceProvider randomEmptySurfaceProvider;
    private final AdjacentRandomEmptySurfaceProvider adjacentRandomEmptySurfaceProvider;

    Surface getEmptySurfaceForType(SurfaceType surfaceType, Collection<Surface> surfaces, GameData gameData) {
        List<Surface> surfacesWithMatchingType = matchingSurfaceTypeFilter.getSurfacesWithMatchingType(surfaces, surfaceType);

        if (surfacesWithMatchingType.isEmpty()) {
            log.debug("There is no surface with type {}. Converting one...", surfaceType);
            Surface randomEmptySurface = randomEmptySurfaceProvider.getEmptyDesertSurface(surfaces, gameData);
            randomEmptySurface.setSurfaceType(surfaceType);
            return randomEmptySurface;
        }

        Optional<Surface> maybeEmptySurfaceWithMatchingType = surfacesWithMatchingType.stream()
            .filter(surface -> gameData.getConstructionAreas().findBySurfaceId(surface.getSurfaceId()).isEmpty())
            .findFirst();
        if (maybeEmptySurfaceWithMatchingType.isPresent()) {
            log.debug("Empty surface found for surfaceType {}: {}", surfaceType, maybeEmptySurfaceWithMatchingType);
            return maybeEmptySurfaceWithMatchingType.get();
        }

        Surface randomEmptySurfaceNextToType = adjacentRandomEmptySurfaceProvider.getRandomEmptySurfaceNextTo(surfacesWithMatchingType, surfaces, gameData);
        randomEmptySurfaceNextToType.setSurfaceType(surfaceType);
        log.debug("Random empty surface next to surfaceType {}: {}", surfaceType, randomEmptySurfaceNextToType);
        return randomEmptySurfaceNextToType;
    }
}
