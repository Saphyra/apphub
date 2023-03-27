package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation.TerraformationProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation.TerraformationProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation.SurfaceToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 436;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int PARALLEL_WORKERS = 254;
    private static final String DATA_ID = "data-id";

    @Mock
    private TerraformingPossibilitiesService terraformingPossibilitiesService;

    @Mock
    private GameDao gameDao;

    @Mock
    private ResourceAllocationService resourceAllocationService;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private SurfaceToQueueItemConverter surfaceToQueueItemConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @Mock
    private TerraformationProcessFactory terraformationProcessFactory;

    @InjectMocks
    private TerraformationService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Mock
    private TerraformationProcess terraformationProcess;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<SurfaceResponse> executionResult;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Constructions constructions;

    @Mock
    private Buildings buildings;

    @Captor
    private ArgumentCaptor<Callable<SurfaceResponse>> argumentCaptor;

    @Test
    public void invalidSurfaceType() {
        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, "afe"));

        ExceptionValidator.validateInvalidParam(ex, "surfaceType", "invalid value");
    }

    @Test
    public void terraformationAlreadyInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(construction));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void surfaceNotEmpty() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(building));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceTypeCannotBeTerraformed() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceCannotBeTerraformedToGivenType() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities()));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void terraform() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities(List.of(terraformingPossibility))));
        given(terraformingPossibility.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(terraformingPossibility.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionFactory.create(SURFACE_ID, ConstructionType.TERRAFORMATION, PLANET_ID, PARALLEL_WORKERS, REQUIRED_WORK_POINTS, SurfaceType.CONCRETE.name())).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(constructionRequirements.getParallelWorkers()).willReturn(PARALLEL_WORKERS);
        given(constructionToModelConverter.convert(GAME_ID, construction)).willReturn(constructionModel);
        given(surfaceToQueueItemConverter.convert(construction, surface)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, gameData, PLANET_ID)).willReturn(queueResponse);
        given(surfaceToResponseConverter.convert(gameData, surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(gameData, PLANET_ID)).willReturn(CollectionUtils.toMap(DATA_ID, planetBuildingOverviewResponse));
        given(terraformationProcessFactory.create(gameData, PLANET_ID, surface, construction)).willReturn(terraformationProcess);
        given(gameData.getProcesses()).willReturn(processes);
        given(terraformationProcess.toModel()).willReturn(processModel);
        given(game.getEventLoop()).willReturn(eventLoop);
        //noinspection unchecked
        given(eventLoop.processWithResponseAndWait(any(Callable.class))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(PLANET_ID, planet, new Planets()));

        SurfaceResponse result = underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name());

        assertThat(result).isEqualTo(surfaceResponse);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture());
        SurfaceResponse executionResponse = argumentCaptor.getValue()
            .call();
        assertThat(executionResponse).isEqualTo(surfaceResponse);

        verify(resourceAllocationService).processResourceRequirements(gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID, Collections.emptyMap());
        verify(gameDataProxy).saveItem(constructionModel, processModel);
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.toMap(DATA_ID, planetBuildingOverviewResponse));
        verify(processes).add(terraformationProcess);
    }
}