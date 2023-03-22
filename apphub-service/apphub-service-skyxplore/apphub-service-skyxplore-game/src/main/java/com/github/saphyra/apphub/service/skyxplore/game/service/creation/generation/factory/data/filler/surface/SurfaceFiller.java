package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SurfaceFiller {
    private final SurfaceFactory surfaceFactory;
    private final SurfaceMapFactory surfaceMapFactory;

    public void fillSurfaces(GameData gameData) {
        gameData.getPlanets()
            .forEach((uuid, planet) -> fillSurfaces(planet, gameData));
    }

    private void fillSurfaces(Planet planet, GameData gameData) {
        log.debug("Generating surfaces...");
        SurfaceType[][] surfaceMap = surfaceMapFactory.createSurfaceMap(planet.getSize());

        Map<Coordinate, Surface> result = new HashMap<>();
        for (int x = 0; x < surfaceMap.length; x++) {
            SurfaceType[] row = surfaceMap[x];
            for (int y = 0; y < row.length; y++) {
                Coordinate coordinate = new Coordinate(x, y);
                SurfaceType surfaceType = row[y];
                Surface surface = surfaceFactory.create(planet.getPlanetId(), surfaceType);
                result.put(coordinate, surface);
            }
        }
        log.debug("Surfaces generated.");
    }
}
