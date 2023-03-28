package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.AllBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction.ConstructionProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
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
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpgradeBuildingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 234;
    private static final Integer REQUIRED_WORK_POINTS = 324;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int PARALLEL_WORKERS = 245;

    @Mock
    private GameDao gameDao;

    @Mock
    private AllBuildingService allBuildingService;

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

    @InjectMocks
    private UpgradeBuildingService underTest;

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
    private BuildingData buildingData;

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
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<SurfaceResponse> executionResult;

    @Mock
    private Constructions constructions;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Captor
    private ArgumentCaptor<Callable<SurfaceResponse>> argumentCaptor;

    @Test
    void deconstructionAlreadyInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(deconstruction));

        Throwable ex = catchThrowable(() -> underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void constructionAlreadyInProgress() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(construction));

        Throwable ex = catchThrowable(() -> underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void buildingAtMaxLevel() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());

        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);

        given(allBuildingService.get(DATA_ID)).willReturn(buildingData);
        given(buildingData.getConstructionRequirements()).willReturn(new HashMap<>());

        Throwable ex = catchThrowable(() -> underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void upgradeBuilding() throws Exception {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(PLANET_ID, planet, new Planets()));

        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);

        given(allBuildingService.get(DATA_ID)).willReturn(buildingData);
        given(buildingData.getConstructionRequirements()).willReturn(CollectionUtils.toMap(LEVEL + 1, constructionRequirements));
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(constructionRequirements.getParallelWorkers()).willReturn(PARALLEL_WORKERS);
        given(constructionFactory.create(BUILDING_ID, ConstructionType.CONSTRUCTION, PLANET_ID, PARALLEL_WORKERS, REQUIRED_WORK_POINTS)).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);

        given(game.getGameId()).willReturn(GAME_ID);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getEventLoop()).willReturn(eventLoop);
        //noinspection unchecked
        given(eventLoop.processWithResponseAndWait(any(Callable.class))).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(surfaceResponse);

        given(buildingToModelConverter.convert(GAME_ID, building)).willReturn(buildingModel);
        given(constructionToModelConverter.convert(GAME_ID, construction)).willReturn(constructionModel);
        given(surfaceToResponseConverter.convert(gameData, surface)).willReturn(surfaceResponse);

        given(buildingConstructionToQueueItemConverter.convert(gameData, construction)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, gameData, PLANET_ID)).willReturn(queueResponse);

        given(constructionProcessFactory.create(gameData, PLANET_ID, building, construction)).willReturn(constructionProcess);
        given(constructionProcess.toModel()).willReturn(processModel);

        SurfaceResponse result = underTest.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        assertThat(result).isEqualTo(surfaceResponse);

        verify(eventLoop).processWithResponseAndWait(argumentCaptor.capture());
        SurfaceResponse executionResponse = argumentCaptor.getValue()
            .call();

        assertThat(executionResponse).isEqualTo(surfaceResponse);

        verify(resourceAllocationService).processResourceRequirements(gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID, Collections.emptyMap());
        verify(constructions).add(construction);
        verify(gameDataProxy).saveItem(buildingModel, constructionModel, processModel);
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);
        verify(processes).add(constructionProcess);
    }
}