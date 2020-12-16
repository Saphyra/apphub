package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet.PlanetFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SolarSystemFactory {
    private final IdGenerator idGenerator;
    private final SolarSystemCoordinateProvider solarSystemCoordinateProvider;
    private final SolarSystemNames solarSystemNames;
    private final PlanetFactory planetFactory;

    public Map<Coordinate, SolarSystem> create(int memberNum, int universeSize, SkyXploreGameCreationSettingsRequest settings) {
        log.info("Generating SolarSystems...");
        List<Coordinate> coordinates = solarSystemCoordinateProvider.getCoordinates(memberNum, universeSize, settings.getSystemAmount());

        List<String> usedSystemNames = new ArrayList<>();
        Map<Coordinate, SolarSystem> result = new HashMap<>();

        for (Coordinate coordinate : coordinates) {
            String systemName = solarSystemNames.getRandomStarName(usedSystemNames);
            usedSystemNames.add(systemName);

            Map<UUID, Planet> planets = planetFactory.create(systemName, settings);

            SolarSystem solarSystem = SolarSystem.builder()
                .solarSystemId(idGenerator.randomUuid())
                .systemName(systemName)
                .coordinate(coordinate)
                .planets(planets)
                .build();

            result.put(solarSystem.getCoordinate(), solarSystem);
        }

        log.info("SolarSystems generated.");
        coordinates.forEach(coordinate -> log.info("SolarSystem generated: {}", coordinate));
        return result;
    }
}
