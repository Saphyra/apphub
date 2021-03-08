package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceBlockFiller {
    private final RandomEmptySlotProvider randomEmptySlotProvider;

    void fillBlockWithSurfaceType(SurfaceType[][] surfaceMap, SurfaceType surfaceType, boolean initialPlacement) {
        Optional<Coordinate> coordinateOptional = initialPlacement
            ? randomEmptySlotProvider.getRandomEmptySlot(surfaceMap)
            : randomEmptySlotProvider.getRandomEmptySlotNextToSurfaceType(surfaceMap, surfaceType);

        coordinateOptional.ifPresent(coordinate -> {
            surfaceMap[(int) coordinate.getX()][(int) coordinate.getY()] = surfaceType;
            log.debug("Coordinate {} filled with surfaceType {}. surfaceMap: {}", coordinate, surfaceType, surfaceMap);
        });
    }
}
