package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.CancelConstructionOfBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConstructBuildingModuleQueueServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 32;
    private static final Integer CURRENT_WORK_POINTS = 254;
    private static final Integer PRIORITY = 4;
    private static final String DATA_ID = "data-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private ConstructionConverter constructionConverter;

    @Mock
    private CancelConstructionOfBuildingModuleService cancelConstructionOfBuildingModuleService;

    @InjectMocks
    private ConstructBuildingModuleQueueService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Game game;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private GameProgressDiff gameProgressDiff;

    @Mock
    private ConstructionModel constructionModel;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(QueueItemType.CONSTRUCT_BUILDING_MODULE);
    }

    @Test
    void getQueue() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.stream()).willReturn(Stream.of(construction));
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(construction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(buildingModules.findById(BUILDING_MODULE_ID)).willReturn(Optional.of(buildingModule));
        given(buildingModules.findByIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(construction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(construction.getPriority()).willReturn(PRIORITY);
        given(buildingModule.getDataId()).willReturn(DATA_ID);

        CustomAssertions.singleListAssertThat(underTest.getQueue(gameData, LOCATION))
            .returns(CONSTRUCTION_ID, QueueItem::getItemId)
            .returns(QueueItemType.CONSTRUCT_BUILDING_MODULE, QueueItem::getType)
            .returns(REQUIRED_WORK_POINTS, QueueItem::getRequiredWorkPoints)
            .returns(CURRENT_WORK_POINTS, QueueItem::getCurrentWorkPoints)
            .returns(PRIORITY, QueueItem::getPriority)
            .returns(Map.of("dataId", DATA_ID), QueueItem::getData);
    }

    @Test
    void setPriority() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(game.getProgressDiff()).willReturn(gameProgressDiff);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(constructionConverter.toModel(GAME_ID, construction)).willReturn(constructionModel);

        underTest.setPriority(USER_ID, LOCATION, CONSTRUCTION_ID, PRIORITY);

        then(construction).should().setPriority(PRIORITY);
        then(gameProgressDiff).should().save(constructionModel);
    }

    @Test
    void cancel() {
        underTest.cancel(USER_ID, LOCATION, CONSTRUCTION_ID);

        then(cancelConstructionOfBuildingModuleService).should().cancelConstruction(USER_ID, CONSTRUCTION_ID);
    }
}