package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurfaceFactory {
    private final IdGenerator idGenerator;
    private final SurfaceMapFactory surfaceMapFactory;
    private final CoordinateModelFactory coordinateModelFactory;

    public Map<Coordinate, Surface> create(UUID gameId, UUID planetId, int planetSize) {
        log.debug("Generating surfaces...");
        SurfaceType[][] surfaceMap = surfaceMapFactory.createSurfaceMap(planetSize);

        Map<Coordinate, Surface> result = new HashMap<>();
        for (int x = 0; x < surfaceMap.length; x++) {
            SurfaceType[] row = surfaceMap[x];
            for (int y = 0; y < row.length; y++) {
                Coordinate coordinate = new Coordinate(x, y);
                SurfaceType surfaceType = row[y];
                Surface surface = Surface.builder()
                    .surfaceId(idGenerator.randomUuid())
                    .planetId(planetId)
                    .coordinate(coordinateModelFactory.create(coordinate, gameId, planetId))
                    .surfaceType(surfaceType)
                    .build();
                result.put(coordinate, surface);
            }
        }
        log.debug("Surfaces generated.");
        return result;
    }
}
