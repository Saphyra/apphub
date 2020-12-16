package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SurfaceFactory {
    private final Random random;
    private final GameCreationProperties properties;
    private final IdGenerator idGenerator;

    public Map<Coordinate, Surface> create(int planetSize) {
        log.debug("Generating surfaces...");
        SurfaceType[][] surfaceMap = createSurfaceMap(planetSize);

        Map<Coordinate, Surface> result = new HashMap<>();
        for (int x = 0; x < surfaceMap.length; x++) {
            SurfaceType[] row = surfaceMap[x];
            for (int y = 0; y < row.length; y++) {
                Coordinate coordinate = new Coordinate(x, y);
                SurfaceType surfaceType = row[y];
                Surface surface = Surface.builder()
                    .surfaceId(idGenerator.randomUuid())
                    .coordinate(coordinate)
                    .surfaceType(surfaceType)
                    .build();
                result.put(coordinate, surface);
            }
        }
        log.debug("Surfaces generated.");
        return result;
    }

    private SurfaceType[][] createSurfaceMap(int planetSize) {
        SurfaceType[][] surfaceMap = createEmptySurfaceMap(planetSize);

        fillSurfaceMap(surfaceMap);
        return surfaceMap;
    }

    private SurfaceType[][] createEmptySurfaceMap(int planetSize) {
        SurfaceType[][] surfaceMap = new SurfaceType[planetSize][planetSize];
        for (int x = 0; x < surfaceMap.length; x++) {
            surfaceMap[x] = new SurfaceType[planetSize];
        }
        return surfaceMap;
    }

    private void fillSurfaceMap(SurfaceType[][] surfaceMap) {
        boolean initialPlacement = true;
        do {
            List<SurfaceType> surfaceTypeList = createSurfaceTypeList(initialPlacement);
            boolean ip = initialPlacement;
            surfaceTypeList.forEach(surfaceType -> fillBlockWithSurfaceType(surfaceMap, surfaceType, ip));
            initialPlacement = false;
        } while (hasNullFields(surfaceMap));
    }

    private boolean hasNullFields(SurfaceType[][] surfaceMap) {
        for (SurfaceType[] surfaceTypes : surfaceMap) {
            for (SurfaceType surfaceType : surfaceTypes) {
                if (isNull(surfaceType)) {
                    log.debug("There are empty fields.");
                    return true;
                }
            }
        }
        log.debug("No more empty fields");
        return false;
    }

    private List<SurfaceType> createSurfaceTypeList(boolean initialPlacement) {
        List<SurfaceType> result = properties.getSurface()
            .getSpawnDetails()
            .stream()
            .flatMap(surfaceTypeSpawnDetails -> Stream.generate(surfaceTypeSpawnDetails::getSurfaceName).limit(surfaceTypeSpawnDetails.getSpawnRate()))
            .map(SurfaceType::valueOf)
            .collect(Collectors.toList());
        Collections.shuffle(result);

        if (initialPlacement) {
            log.debug("Initial placement. Filtering out optional surfaceTypes");
            List<GameCreationProperties.SurfaceTypeSpawnDetails> optionalSpawnDetails = properties.getSurface().getSpawnDetails().stream()
                .filter(GameCreationProperties.SurfaceTypeSpawnDetails::isOptional)
                .collect(Collectors.toList());

            optionalSpawnDetails.stream()
                .filter((s) -> random.randBoolean())
                .peek(surfaceTypeSpawnDetails -> log.debug("SurfaceType {} will not spawn", surfaceTypeSpawnDetails.getSurfaceName()))
                .forEach(surfaceTypeSpawnDetails -> result.removeIf(surfaceType -> surfaceType.equals(SurfaceType.valueOf(surfaceTypeSpawnDetails.getSurfaceName()))));

        }
        log.debug("surfaceTypeList: {}", result);
        return result;
    }

    private void fillBlockWithSurfaceType(SurfaceType[][] surfaceMap, SurfaceType surfaceType, boolean initialPlacement) {
        Optional<Coordinate> coordinateOptional = initialPlacement ? getRandomEmptySlot(surfaceMap) : getRandomEmptySlotNextToSurfaceType(surfaceMap, surfaceType);
        coordinateOptional.ifPresent(coordinate -> {
            surfaceMap[(int) coordinate.getX()][(int) coordinate.getY()] = surfaceType;
            log.debug("Coordinate {} filled with surfaceType {}. surfaceMap: {}", coordinate, surfaceType, surfaceMap);
        });
    }

    private Optional<Coordinate> getRandomEmptySlot(SurfaceType[][] surfaceMap) {
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

    private boolean isEmptySlot(SurfaceType[][] surfaceMap, Coordinate coordinate) {
        return isNull(surfaceMap[(int) coordinate.getX()][(int) coordinate.getY()]);
    }

    private Optional<Coordinate> getRandomEmptySlotNextToSurfaceType(SurfaceType[][] surfaceMap, SurfaceType surfaceType) {
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
}
