package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.DefaultFoodFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.PriorityFillerService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building.BuildingFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population.PopulationFillerService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.solar_system.SolarSystemFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.SurfaceFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.NewbornSolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.SolarSystemGenerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameDataFactoryTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer UNIVERSE_SIZE = 2453;

    @Mock
    private SolarSystemGenerationService solarSystemGenerationService;

    @Mock
    private UniverseSizeCalculator universeSizeCalculator;

    @Mock
    private SolarSystemFiller solarSystemFiller;

    @Mock
    private SurfaceFiller surfaceFiller;

    @Mock
    private BuildingFiller buildingFiller;

    @Mock
    private DefaultFoodFiller defaultFoodFiller;

    @Mock
    private PopulationFillerService populationFillerService;

    @Mock
    private PriorityFillerService priorityFillerService;

    @InjectMocks
    private GameDataFactory underTest;

    @Mock
    private Player player;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private NewbornSolarSystem newbornSolarSystem;

    @Test
    void create() {
        given(solarSystemGenerationService.generateSolarSystems(List.of(player), settings)).willReturn(List.of(newbornSolarSystem));
        given(universeSizeCalculator.calculate(List.of(newbornSolarSystem))).willReturn(UNIVERSE_SIZE);

        GameData result = underTest.create(GAME_ID, List.of(player), settings);

        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getUniverseSize()).isEqualTo(UNIVERSE_SIZE);

        verify(solarSystemFiller).fillNewbornSolarSystems(List.of(newbornSolarSystem), result, settings);
        verify(surfaceFiller).fillSurfaces(result);
        verify(buildingFiller).fillBuildings(result);
        verify(defaultFoodFiller).fillDefaultFood(result);
        verify(populationFillerService).fillPopulation(result);
        verify(priorityFillerService).fillPriorities(result);
    }
}