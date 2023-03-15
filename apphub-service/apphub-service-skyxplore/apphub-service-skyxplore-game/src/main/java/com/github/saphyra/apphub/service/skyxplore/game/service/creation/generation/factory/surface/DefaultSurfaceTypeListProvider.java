package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
class DefaultSurfaceTypeListProvider {
    private final List<SurfaceType> surfaceTypes;

    DefaultSurfaceTypeListProvider(GameProperties properties) {
        surfaceTypes = properties.getSurface()
            .getSpawnDetails()
            .stream()
            .flatMap(surfaceTypeSpawnDetails -> Stream.generate(surfaceTypeSpawnDetails::getSurfaceName).limit(surfaceTypeSpawnDetails.getSpawnRate()))
            .map(SurfaceType::valueOf)
            .collect(Collectors.toList());
    }

    List<SurfaceType> getSurfaceTypes() {
        return new ArrayList<>(surfaceTypes);
    }
}
