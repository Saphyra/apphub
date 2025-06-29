package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class MapWeighter {
    private final GameProperties gameProperties;

    Map<Coordinate, Integer> getWeightedMap(GameData gameData, UUID location) {
        Collection<Surface> surfaces = gameData.getSurfaces()
            .getByPlanetId(location);
        Map<SurfaceType, Integer> logisticsWeight = gameProperties.getSurface()
            .getLogisticsWeight();

        return surfaces.stream()
            .collect(Collectors.toMap(surface -> gameData.getCoordinates().findByReferenceIdValidated(surface.getSurfaceId()), surface -> logisticsWeight.get(surface.getSurfaceType())));
    }
}
