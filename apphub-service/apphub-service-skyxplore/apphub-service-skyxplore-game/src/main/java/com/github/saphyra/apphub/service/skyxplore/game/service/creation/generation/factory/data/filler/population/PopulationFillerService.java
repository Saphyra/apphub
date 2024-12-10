package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PopulationFillerService {
    private final CitizenFactory citizenFactory;
    private final StorageCalculator storageCalculator;

    public void fillPopulation(GameData gameData) {
        gameData.getPlanets()
            .values()
            .stream()
            .filter(Planet::hasOwner)
            .forEach(planet -> fillPopulation(planet, gameData));
    }

    private void fillPopulation(Planet planet, GameData gameData) {
        int capacity = storageCalculator.calculateDwellingCapacity(gameData, planet.getPlanetId());

        for (int i = 0; i < capacity; i++) {
            citizenFactory.addToGameData(planet.getPlanetId(), gameData);
        }
    }
}
