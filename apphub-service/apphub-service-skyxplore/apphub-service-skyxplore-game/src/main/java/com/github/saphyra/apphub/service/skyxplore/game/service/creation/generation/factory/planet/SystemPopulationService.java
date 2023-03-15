package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.planet;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import lombok.Builder;
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
@Builder
public class SystemPopulationService {
    private final Random random;
    private final GameProperties properties;
    private final PlanetCoordinateProvider coordinateProvider;
    private final ExecutorServiceBean executorServiceBean;
    private final PlanetFactory planetFactory;

    public Map<UUID, Planet> populateSystemWithPlanets(UUID gameId, UUID solarSystemId, String systemName, int systemRadius, int expectedPlanetAmount, SkyXploreGameCreationSettingsRequest settings) {
        List<Coordinate> coordinates = coordinateProvider.getCoordinates(expectedPlanetAmount, systemRadius);
        log.debug("Planet coordinates generated.");

        Range<Integer> planetSizeRange = properties.getPlanet()
            .getPlanetSize()
            .get(settings.getPlanetSize());

        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < expectedPlanetAmount; i++) {
            indexList.add(i);
        }

        Map<UUID, Planet> result = executorServiceBean.processCollectionWithWait(indexList, planetIndex -> planetFactory.create(gameId, planetIndex, coordinates.get(planetIndex), solarSystemId, systemName, planetSizeRange))
            .stream()
            .collect(Collectors.toMap(Planet::getPlanetId, Function.identity()));

        log.debug("Planets generated.");
        return result;
    }
}
