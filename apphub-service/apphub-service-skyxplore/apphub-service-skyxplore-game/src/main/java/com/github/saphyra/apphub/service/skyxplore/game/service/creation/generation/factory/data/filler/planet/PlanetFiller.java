package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.planet;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReferredCoordinateFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlanetFiller {
    private final RandomCoordinateProvider randomCoordinateProvider;
    private final PlanetFactory planetFactory;
    private final ReferredCoordinateFactory referredCoordinateFactory;

    public void fillPlanets(SolarSystem solarSystem, Map<Double, UUID> planets, GameData gameData, SkyXploreGameSettings settings) {
        List<Double> sortedCoordinates = planets.keySet()
            .stream()
            .sorted(Comparator.comparingDouble(value -> value))
            .toList();

        for (int planetIndex = 0; planetIndex < sortedCoordinates.size(); planetIndex++) {
            Double orbitRadius = sortedCoordinates.get(planetIndex);
            Coordinate coordinate = randomCoordinateProvider.getCoordinateOnCircle(orbitRadius);
            Planet planet = planetFactory.create(
                solarSystem,
                planetIndex,
                settings,
                planets.get(orbitRadius),
                orbitRadius
            );
            ReferredCoordinate planetCoordinate = referredCoordinateFactory.create(planet.getPlanetId(), coordinate);

            gameData.getPlanets()
                .put(planet.getPlanetId(), planet);
            gameData.getCoordinates()
                .add(planetCoordinate);
        }
    }
}
