package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class RandomEmptySlotProvider {
    private final Random random;

    Optional<Coordinate> getRandomEmptySlot(SurfaceType[][] surfaceMap) {
        Coordinate coordinate;
        do {
            coordinate = new Coordinate(
                random.randInt(0, surfaceMap.length - 1),
                random.randInt(0, surfaceMap.length - 1)
            );
        } while (!isEmptySlot(surfaceMap, coordinate));
        log.debug("Random empty slot selected: {}", coordinate);
        return Optional.of(coordinate);
    }

    Optional<Coordinate> getRandomEmptySlotNextToSurfaceType(SurfaceType[][] surfaceMap, SurfaceType surfaceType) {
        List<Coordinate> emptySlotsNextToSurfaceType = getEmptySlotsNextToSurfaceType(surfaceMap, surfaceType);

        if (emptySlotsNextToSurfaceType.isEmpty()) {
            log.debug("No more places for surfaceType {}", surfaceType);
            return Optional.empty();
        } else {
            Coordinate coordinate = emptySlotsNextToSurfaceType.get(random.randInt(0, emptySlotsNextToSurfaceType.size() - 1));
            log.debug("Random empty slot next to surfaceType {}: {}", surfaceType, coordinate);
            return Optional.of(coordinate);
        }
    }

    private List<Coordinate> getEmptySlotsNextToSurfaceType(SurfaceType[][] surfaceMap, SurfaceType surfaceType) {
        Set<Coordinate> slotsNextToSurfaceType = new HashSet<>();
        for (int x = 0; x < surfaceMap.length; x++) {
            SurfaceType[] surfaceTypes = surfaceMap[x];
            for (int y = 0; y < surfaceTypes.length; y++) {
                if (surfaceMap[x][y] == surfaceType) {
                    slotsNextToSurfaceType.add(new Coordinate(x - 1, y));
                    slotsNextToSurfaceType.add(new Coordinate(x + 1, y));
                    slotsNextToSurfaceType.add(new Coordinate(x, y - 1));
                    slotsNextToSurfaceType.add(new Coordinate(x, y + 1));
                }
            }
        }
        List<Coordinate> result = slotsNextToSurfaceType.stream()
            .filter(coordinate -> coordinate.getX() >= 0 && coordinate.getX() < surfaceMap.length)
            .filter(coordinate -> coordinate.getY() >= 0 && coordinate.getY() < surfaceMap.length)
            .filter(coordinate -> isEmptySlot(surfaceMap, coordinate))
            .collect(Collectors.toList());
        log.debug("Empty slots next to surfaceType {}: {}", surfaceType, slotsNextToSurfaceType);
        return result;
    }

    private boolean isEmptySlot(SurfaceType[][] surfaceMap, Coordinate coordinate) {
        return isNull(surfaceMap[(int) coordinate.getX()][(int) coordinate.getY()]);
    }
}
