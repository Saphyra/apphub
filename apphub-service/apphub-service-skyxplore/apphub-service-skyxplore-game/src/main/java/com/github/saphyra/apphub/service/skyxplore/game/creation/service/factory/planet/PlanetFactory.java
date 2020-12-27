package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
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
public class PlanetFactory {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVXYZ";

    private final IdGenerator idGenerator;
    private final Random random;
    private final GameCreationProperties properties;
    private final SurfaceFactory surfaceFactory;
    private final PlanetCoordinateProvider coordinateProvider;
    private final ExecutorServiceBean executorServiceBean;

    public Map<UUID, Planet> create(UUID solarSystemId, String systemName, int systemRadius, SkyXploreGameCreationSettingsRequest settings) {
        log.debug("Generating planets for system {} with radius {}", systemName, systemRadius);
        Range<Integer> range = properties.getPlanet().getSystemSize().get(settings.getSystemSize());

        int expectedPlanetAmount = random.randInt(range.getMin(), range.getMax());
        log.debug("Expected planet amount: {}", expectedPlanetAmount);

        List<Coordinate> coordinates = coordinateProvider.getCoordinates(expectedPlanetAmount, systemRadius);
        log.debug("Planet coordinates generated.");

        Range<Integer> planetSizeRange = properties.getPlanet().getPlanetSize().get(settings.getPlanetSize());

        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < expectedPlanetAmount; i++) {
            indexList.add(i);
        }

        Map<UUID, Planet> result = executorServiceBean.processCollectionWithWait(indexList, planetIndex -> createPlanet(planetIndex, coordinates.get(planetIndex), solarSystemId, systemName, planetSizeRange))
            .stream()
            .collect(Collectors.toMap(Planet::getPlanetId, Function.identity()));

        log.debug("Planets generated.");
        return result;
    }

    private Planet createPlanet(Integer planetIndex, Coordinate coordinate, UUID solarSystemId, String systemName, Range<Integer> planetSizeRange) {
        int planetSize = random.randInt(planetSizeRange.getMin(), planetSizeRange.getMax());

        Map<Coordinate, Surface> surfaces = surfaceFactory.create(planetSize);

        return Planet.builder()
            .planetId(idGenerator.randomUuid())
            .solarSystemId(solarSystemId)
            .coordinate(coordinate)
            .planetName(String.format("%s %s", systemName, ALPHABET.charAt(planetIndex)))
            .size(planetSize)
            .surfaces(surfaces)
            .build();
    }
}
