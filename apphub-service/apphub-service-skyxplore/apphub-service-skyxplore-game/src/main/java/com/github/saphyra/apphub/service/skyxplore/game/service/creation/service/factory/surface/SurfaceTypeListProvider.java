package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceTypeListProvider {
    private final Random random;
    private final GameCreationProperties properties;
    private final DefaultSurfaceTypeListProvider defaultSurfaceTypeListProvider;

    List<SurfaceType> createSurfaceTypeList(boolean initialPlacement) {
        List<SurfaceType> result = defaultSurfaceTypeListProvider.getSurfaceTypes();
        Collections.shuffle(result);

        if (initialPlacement) {
            log.debug("Initial placement. Filtering out optional surfaceTypes");
            properties.getSurface()
                .getSpawnDetails()
                .stream()
                .filter(GameCreationProperties.SurfaceTypeSpawnDetails::isOptional)
                .filter((s) -> random.randBoolean())
                .peek(surfaceTypeSpawnDetails -> log.debug("SurfaceType {} will not spawn", surfaceTypeSpawnDetails.getSurfaceName()))
                .forEach(surfaceTypeSpawnDetails -> result.removeIf(surfaceType -> surfaceType.equals(SurfaceType.valueOf(surfaceTypeSpawnDetails.getSurfaceName()))));

        }
        log.debug("surfaceTypeList: {}", result);
        return result;
    }
}
