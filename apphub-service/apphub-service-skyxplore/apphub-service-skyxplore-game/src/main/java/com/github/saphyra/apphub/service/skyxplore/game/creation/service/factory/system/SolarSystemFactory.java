package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet.SystemPopulationService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
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
//TODO unit test
public class SolarSystemFactory {
    private final IdGenerator idGenerator;
    private final SolarSystemCoordinateProvider solarSystemCoordinateProvider;
    private final SolarSystemNames solarSystemNames;
    private final SystemPopulationService systemPopulationService;
    private final Random random;
    private final GameCreationProperties properties;
    private final ExecutorServiceBean executorServiceBean;

    public Map<Coordinate, SolarSystem> create(int memberNum, int universeSize, SkyXploreGameCreationSettingsRequest settings) {
        log.info("Generating SolarSystems...");

        List<String> usedSystemNames = new ArrayList<>();

        Map<Coordinate, String> coordinates = solarSystemCoordinateProvider.getCoordinates(memberNum, universeSize, settings.getSystemAmount())
            .stream()
            .collect(Collectors.toMap(Function.identity(), o -> {
                String name = solarSystemNames.getRandomStarName(usedSystemNames);
                usedSystemNames.add(name);
                return name;
            }));

        Map<Coordinate, SolarSystem> result = executorServiceBean.processCollectionWithWait(coordinates.entrySet(), entry -> generateSolarSystem(settings, entry.getValue(), entry.getKey()))
            .stream()
            .collect(Collectors.toMap(SolarSystem::getCoordinate, Function.identity()));

        log.info("SolarSystems generated.");
        if (log.isDebugEnabled()) {
            coordinates.forEach((key, value) -> log.debug("SolarSystem generated with name {} and coordinate {}", value, key));
        }
        return result;
    }

    private SolarSystem generateSolarSystem(SkyXploreGameCreationSettingsRequest settings, String systemName, Coordinate coordinate) {
        log.debug("Generating SolarSystem for coordinate {}", coordinate);
        Range<Integer> range = properties.getSolarSystem()
            .getRadius()
            .get(settings.getSystemSize());
        int systemRadius = random.randInt(range.getMin(), range.getMax());

        UUID solarSystemId = idGenerator.randomUuid();
        Map<UUID, Planet> planets = systemPopulationService.populateSystemWithPlanets(solarSystemId, systemName, systemRadius, settings);
        return SolarSystem.builder()
            .solarSystemId(solarSystemId)
            .radius(systemRadius)
            .defaultName(systemName)
            .coordinate(coordinate)
            .planets(planets)
            .build();
    }
}
