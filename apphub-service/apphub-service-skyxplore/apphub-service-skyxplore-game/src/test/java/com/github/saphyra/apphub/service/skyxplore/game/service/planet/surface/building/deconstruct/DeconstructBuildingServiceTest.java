package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction.DeconstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction.DeconstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction.BuildingDeconstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.DeconstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructBuildingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private DeconstructionFactory deconstructionFactory;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @Mock
    private DeconstructionProcessFactory deconstructionProcessFactory;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private BuildingToModelConverter buildingToModelConverter;

    @Mock
    private DeconstructionToModelConverter deconstructionToModelConverter;

    @InjectMocks
    private DeconstructBuildingService underTest;

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
    private Deconstruction deconstruction;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<SurfaceResponse> executionResult;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Mock
    private DeconstructionProcess deconstructionProcess;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Captor
    private ArgumentCaptor<Callable<SurfaceResponse>> argumentCaptor;

    @Test
    void constructionInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(CollectionUtils.toMap(GameConstants.ORIGO, surface, new SurfaceMap()));
        given(surface.getBuilding()).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(building.getConstruction()).willReturn(construction);

        Throwable ex = catchThrowable(() -> underTest.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    void deconstructBuilding() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(CollectionUtils.toMap(GameConstants.ORIGO, surface, new SurfaceMap()));
        given(surface.getBuilding()).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(deconstructionFactory.create(BUILDING_ID)).willReturn(deconstruction);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithResponseAndWait(any(Callable.class))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);
        given(buildingDeconstructionToQueueItemConverter.convert(building)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, planet)).willReturn(queueResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(Map.of(DATA_ID, planetBuildingOverviewResponse));
        given(deconstructionProcessFactory.create(game, planet, deconstruction)).willReturn(deconstructionProcess);
        given(game.getProcesses()).willReturn(processes);
        given(game.getGameId()).willReturn(GAME_ID);
        given(buildingToModelConverter.convert(building, GAME_ID)).willReturn(buildingModel);
        given(deconstructionToModelConverter.convert(deconstruction, GAME_ID)).willReturn(deconstructionModel);
        given(deconstructionProcess.toModel()).willReturn(processModel);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        assertThat(result).isEqualTo(surfaceResponse);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture());
        SurfaceResponse returnedSurfaceResponse = argumentCaptor.getValue()
            .call();

        assertThat(returnedSurfaceResponse).isEqualTo(surfaceResponse);

        verify(building).setDeconstruction(deconstruction);
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, Map.of(DATA_ID, planetBuildingOverviewResponse));
        verify(processes).add(deconstructionProcess);
        verify(gameDataProxy).saveItem(buildingModel, deconstructionModel, processModel);
    }
}