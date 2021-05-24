package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceMapFiller {
    private final SurfaceTypeListProvider surfaceTypeListProvider;
    private final SurfaceBlockFiller surfaceBlockFiller;

    void fillSurfaceMap(SurfaceType[][] surfaceMap) {
        boolean initialPlacement = true;
        do {
            List<SurfaceType> surfaceTypeList = surfaceTypeListProvider.createSurfaceTypeList(initialPlacement);
            boolean ip = initialPlacement;
            surfaceTypeList.forEach(surfaceType -> surfaceBlockFiller.fillBlockWithSurfaceType(surfaceMap, surfaceType, ip));
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
}
