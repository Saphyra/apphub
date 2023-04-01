package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReferredCoordinateFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SurfaceFiller {
    private final SurfaceFactory surfaceFactory;
    private final SurfaceMapFactory surfaceMapFactory;
    private final ReferredCoordinateFactory referredCoordinateFactory;

    public void fillSurfaces(GameData gameData) {
        gameData.getPlanets()
            .forEach((uuid, planet) -> fillSurfaces(planet, gameData));
    }

    private void fillSurfaces(Planet planet, GameData gameData) {
        log.debug("Generating surfaces...");
        SurfaceType[][] surfaceMap = surfaceMapFactory.createSurfaceMap(planet.getSize());

        for (int x = 0; x < surfaceMap.length; x++) {
            SurfaceType[] row = surfaceMap[x];
            for (int y = 0; y < row.length; y++) {
                Coordinate coordinate = new Coordinate(x, y);
                SurfaceType surfaceType = row[y];
                Surface surface = surfaceFactory.create(planet.getPlanetId(), surfaceType);

                ReferredCoordinate referredCoordinate = referredCoordinateFactory.create(surface.getSurfaceId(), coordinate);

                gameData.getSurfaces()
                    .add(surface);
                gameData.getCoordinates()
                    .add(referredCoordinate);
            }
        }

        log.debug("Surfaces generated.");
    }
}
