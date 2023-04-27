package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.PriorityFillerService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building.BuildingFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.DefaultFoodFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population.PopulationFillerService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.solar_system.SolarSystemFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.SurfaceFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.NewbornSolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.SolarSystemGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDataFactory {
    private final SolarSystemGenerationService solarSystemGenerationService;
    private final UniverseSizeCalculator universeSizeCalculator;
    private final SolarSystemFiller solarSystemFiller;
    private final SurfaceFiller surfaceFiller;
    private final BuildingFiller buildingFiller;
    private final DefaultFoodFiller defaultFoodFiller;
    private final PopulationFillerService populationFillerService;
    private final PriorityFillerService priorityFillerService;

    public GameData create(UUID gameId, Collection<Player> players, SkyXploreGameSettings settings) {
        List<NewbornSolarSystem> newbornSolarSystems = solarSystemGenerationService.generateSolarSystems(players, settings);

        GameData gameData = GameData.builder()
            .gameId(gameId)
            .universeSize(universeSizeCalculator.calculate(newbornSolarSystems))
            .build();

        solarSystemFiller.fillNewbornSolarSystems(newbornSolarSystems, gameData, settings);
        surfaceFiller.fillSurfaces(gameData);
        buildingFiller.fillBuildings(gameData);
        defaultFoodFiller.fillDefaultFood(gameData);
        populationFillerService.fillPopulation(gameData);
        priorityFillerService.fillPriorities(gameData);

        return gameData;
    }
}
