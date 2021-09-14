package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system.SolarSystemGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UniverseFactory {
    private final SolarSystemGeneratorService solarSystemGeneratorService;
    private final UniverseSizeCalculator universeSizeCalculator;

    public Universe create(UUID gameId, int playerCount, SkyXploreGameCreationSettingsRequest settings) {
        log.info("Creating universe...");
        List<SolarSystem> solarSystems = solarSystemGeneratorService.generateSolarSystems(gameId, playerCount, settings);
        int universeSize = universeSizeCalculator.calculateUniverseSize(solarSystems);
        log.info("UniverseSize: {}", universeSize);

        Map<Coordinate, SolarSystem> solarSystemMapping = solarSystems
            .stream()
            .collect(Collectors.toMap(solarSystem -> solarSystem.getCoordinate().getCoordinate(), Function.identity()));

        log.info("Universe created.");
        return Universe.builder()
            .size(universeSize)
            .systems(solarSystemMapping)
            .connections(new ArrayList<>())
            .build();
    }
}
