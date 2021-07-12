package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet.SystemPopulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemFactory {
    private final IdGenerator idGenerator;
    private final SystemPopulationService systemPopulationService;
    private final Random random;
    private final GameCreationProperties properties;
    private final CoordinateModelFactory coordinateModelFactory;

    SolarSystem create(UUID gameId, SkyXploreGameCreationSettingsRequest settings, String systemName, Coordinate coordinate) {
        log.debug("Generating SolarSystem for coordinate {}", coordinate);
        Range<Integer> range = properties.getSolarSystem()
            .getRadius()
            .get(settings.getSystemSize());
        int systemRadius = random.randInt(range.getMin(), range.getMax());

        UUID solarSystemId = idGenerator.randomUuid();
        Map<UUID, Planet> planets = systemPopulationService.populateSystemWithPlanets(gameId, solarSystemId, systemName, systemRadius, settings);
        return SolarSystem.builder()
            .solarSystemId(solarSystemId)
            .radius(systemRadius)
            .defaultName(systemName)
            .coordinate(coordinateModelFactory.create(coordinate, gameId, solarSystemId))
            .planets(planets)
            .build();
    }
}
