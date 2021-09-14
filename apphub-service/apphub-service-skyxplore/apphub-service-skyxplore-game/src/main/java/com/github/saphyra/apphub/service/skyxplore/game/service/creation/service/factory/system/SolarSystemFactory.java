package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemFactory {
    private final CoordinateModelFactory coordinateModelFactory;
    private final SolarSystemCoordinateProvider solarSystemCoordinateProvider;

    SolarSystem create(UUID gameId, UUID solarSystemId, String systemName, int radius, List<SolarSystem> solarSystems, Map<UUID, Planet> planets) {
        Coordinate coordinate = solarSystemCoordinateProvider.getCoordinate(solarSystems);

        log.debug("Generating SolarSystem for coordinate {}", coordinate);
        return SolarSystem.builder()
            .solarSystemId(solarSystemId)
            .radius(radius)
            .defaultName(systemName)
            .coordinate(coordinateModelFactory.create(coordinate, gameId, solarSystemId))
            .planets(planets)
            .build();
    }
}
