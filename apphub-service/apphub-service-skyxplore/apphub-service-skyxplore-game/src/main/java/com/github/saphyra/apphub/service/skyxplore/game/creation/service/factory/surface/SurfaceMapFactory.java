package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SurfaceMapFactory {
    private final EmptySurfaceMapFactory emptySurfaceMapFactory;
    private final SurfaceMapFiller surfaceMapFiller;

    SurfaceType[][] createSurfaceMap(int planetSize) {
        SurfaceType[][] surfaceMap = emptySurfaceMapFactory.createEmptySurfaceMap(planetSize);

        surfaceMapFiller.fillSurfaceMap(surfaceMap);
        return surfaceMap;
    }
}