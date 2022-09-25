package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.AllBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction.ConstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.BuildingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConstructNewBuildingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 2314;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int PARALLEL_WORKERS = 42;

    @Mock
    private GameDao gameDao;

    @Mock
    private AllBuildingService allBuildingService;

    @Mock
    private BuildingFactory buildingFactory;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private ResourceAllocationService resourceAllocationService;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private BuildingToModelConverter buildingToModelConverter;

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private BuildingConstructionToQueueItemConverter buildingConstructionToQueueItemConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private ConstructionProcessFactory constructionProcessFactory;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @InjectMocks
    private ConstructNewBuildingService underTest;

    @Mock
    private BuildingData buildingData;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private ConstructionProcess constructionProcess;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Before
    public void setUp() {
        given(allBuildingService.getOptional(DATA_ID)).willReturn(Optional.of(buildingData));
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface)));
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
    }

    @Test
    public void invalidDataId() {
        given(allBuildingService.getOptional(DATA_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "invalid value");
    }

    @Test
    public void terraformationInProgress() {
        given(surface.getTerraformation()).willReturn(construction);

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void surfaceAlreadyOccupied() {
        given(surface.getBuilding()).willReturn(building);

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void buildingCannotBeBuiltToGivenSurfaceType() {
        given(buildingData.getPlaceableSurfaceTypes()).willReturn(Collections.emptyList());

        Throwable ex = catchThrowable(() -> underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void constructNewBuilding() {
        given(buildingData.getPlaceableSurfaceTypes()).willReturn(List.of(SurfaceType.CONCRETE));
        given(buildingData.getConstructionRequirements()).willReturn(CollectionUtils.singleValueMap(1, constructionRequirements));
        given(buildingFactory.create(DATA_ID, SURFACE_ID, 0)).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionRequirements.getParallelWorkers()).willReturn(PARALLEL_WORKERS);
        given(constructionFactory.create(BUILDING_ID, PARALLEL_WORKERS, REQUIRED_WORK_POINTS)).willReturn(construction);
        given(game.getGameId()).willReturn(GAME_ID);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(buildingToModelConverter.convert(building, GAME_ID)).willReturn(buildingModel);
        given(constructionToModelConverter.convert(construction, GAME_ID)).willReturn(constructionModel);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(buildingConstructionToQueueItemConverter.convert(building)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, planet)).willReturn(queueResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));

        given(constructionProcessFactory.create(game, planet, building)).willReturn(constructionProcess);
        given(game.getProcesses()).willReturn(processes);
        given(constructionProcess.toModel()).willReturn(processModel);

        SurfaceResponse result = underTest.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID);

        assertThat(result).isEqualTo(surfaceResponse);

        verify(building).setConstruction(construction);
        verify(surface).setBuilding(building);
        verify(resourceAllocationService).processResourceRequirements(GAME_ID, planet, LocationType.PLANET, CONSTRUCTION_ID, Collections.emptyMap());
        verify(gameDataProxy).saveItem(buildingModel, constructionModel, processModel);
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));
        verify(processes).add(constructionProcess);
    }
}