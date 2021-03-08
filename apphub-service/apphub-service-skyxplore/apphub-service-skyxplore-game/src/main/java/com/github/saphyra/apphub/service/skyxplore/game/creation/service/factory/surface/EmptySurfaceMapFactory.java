package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EmptySurfaceMapFactory {
    SurfaceType[][] createEmptySurfaceMap(int planetSize) {
        SurfaceType[][] surfaceMap = new SurfaceType[planetSize][planetSize];
        for (int x = 0; x < surfaceMap.length; x++) {
            surfaceMap[x] = new SurfaceType[planetSize];
        }
        return surfaceMap;
    }
}
