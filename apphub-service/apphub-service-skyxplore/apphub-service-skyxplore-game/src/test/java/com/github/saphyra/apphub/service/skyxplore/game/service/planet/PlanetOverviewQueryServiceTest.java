package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.*;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority.PriorityQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueFacade;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.BuildingsSummaryQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlanetOverviewQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String PLANET_NAME = "planet-name";
    private static final String STORAGE_TYPE = "storage-type";
    private static final String PRIORITY_TYPE = "priority-type";
    private static final Integer PRIORITY_VALUE = 24;
    private static final String SURFACE_TYPE = "surface-type";

    @Mock
    private GameDao gameDao;

    @Mock
    private PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @Mock
    private SurfaceResponseQueryService surfaceResponseQueryService;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @Mock
    private PriorityQueryService priorityQueryService;

    @Mock
    private QueueFacade queueFacade;

    @Mock
    private BuildingsSummaryQueryService buildingsSummaryQueryService;

    @InjectMocks
    private PlanetOverviewQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetStorageResponse storageResponse;

    @Mock
    private PlanetPopulationOverviewResponse populationResponse;

    @Mock
    private PlanetBuildingOverviewResponse buildingResponse;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private ConstructionAreaOverviewResponse constructionAreaOverviewResponse;

    @BeforeEach
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(surfaceResponseQueryService.getSurfaceOfPlanet(USER_ID, PLANET_ID)).willReturn(List.of(surfaceResponse));
        given(planetStorageOverviewQueryService.getStorage(USER_ID, PLANET_ID)).willReturn(storageResponse);
        given(planetPopulationOverviewQueryService.getPopulationOverview(USER_ID, PLANET_ID)).willReturn(populationResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(USER_ID, PLANET_ID)).willReturn(Map.of(STORAGE_TYPE, buildingResponse));
        given(priorityQueryService.getPriorities(USER_ID, PLANET_ID)).willReturn(Map.of(PRIORITY_TYPE, PRIORITY_VALUE));
        given(queueFacade.getQueueOfPlanet(USER_ID, PLANET_ID)).willReturn(List.of(queueResponse));
        given(buildingsSummaryQueryService.getBuildingsSummary(USER_ID, PLANET_ID)).willReturn(Map.of(SURFACE_TYPE, List.of(constructionAreaOverviewResponse)));
    }

    @Test
    public void getDefaultName() {
        given(planet.getCustomNames()).willReturn(new OptionalHashMap<>());
        given(planet.getDefaultName()).willReturn(PLANET_NAME);

        PlanetOverviewResponse result = underTest.getOverview(USER_ID, PLANET_ID);

        assertThat(result.getPlanetName()).isEqualTo(PLANET_NAME);
        assertThat(result.getSurfaces()).containsExactly(surfaceResponse);
        assertThat(result.getStorage()).isEqualTo(storageResponse);
        assertThat(result.getPopulation()).isEqualTo(populationResponse);
        assertThat(result.getBuildings()).containsEntry(STORAGE_TYPE, buildingResponse);
        assertThat(result.getPriorities()).containsEntry(PRIORITY_TYPE, PRIORITY_VALUE);
        assertThat(result.getQueue()).containsExactly(queueResponse);
        assertThat(result.getBuildingsSummary()).isEqualTo(Map.of(SURFACE_TYPE, List.of(constructionAreaOverviewResponse)));
    }

    @Test
    public void getCustomName() {
        given(planet.getCustomNames()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(USER_ID, PLANET_NAME)));

        PlanetOverviewResponse result = underTest.getOverview(USER_ID, PLANET_ID);

        assertThat(result.getPlanetName()).isEqualTo(PLANET_NAME);
        assertThat(result.getSurfaces()).containsExactly(surfaceResponse);
        assertThat(result.getStorage()).isEqualTo(storageResponse);
        assertThat(result.getPopulation()).isEqualTo(populationResponse);
        assertThat(result.getBuildings()).containsEntry(STORAGE_TYPE, buildingResponse);
        assertThat(result.getPriorities()).containsEntry(PRIORITY_TYPE, PRIORITY_VALUE);
        assertThat(result.getQueue()).containsExactly(queueResponse);
        assertThat(result.getBuildingsSummary()).isEqualTo(Map.of(SURFACE_TYPE, List.of(constructionAreaOverviewResponse)));
    }
}