package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.common.CancelDeconstructionFacade;
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
class DeconstructConstructionAreaQueueServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 32;
    private static final Integer CURRENT_WORK_POINTS = 254;
    private static final Integer PRIORITY = 4;
    private static final String DATA_ID = "data-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @Mock
    private GameDao gameDao;

    @Mock
    private DeconstructionConverter deconstructionConverter;

    @Mock
    private CancelDeconstructionFacade cancelDeconstructionFacade;

    @InjectMocks
    private DeconstructConstructionAreaQueueService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private Game game;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private GameProgressDiff gameProgressDiff;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Mock
    private DeconstructionProperties deconstructionProperties;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(QueueItemType.DECONSTRUCT_CONSTRUCTION_AREA);
    }

    @Test
    void getQueue() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.stream()).willReturn(Stream.of(deconstruction));
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(constructionAreas.findByConstructionAreaId(BUILDING_MODULE_ID)).willReturn(Optional.of(constructionArea));
        given(constructionAreas.findByIdValidated(BUILDING_MODULE_ID)).willReturn(constructionArea);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(gameProperties.getDeconstruction()).willReturn(deconstructionProperties);
        given(deconstructionProperties.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(deconstruction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(deconstruction.getPriority()).willReturn(PRIORITY);
        given(constructionArea.getDataId()).willReturn(DATA_ID);

        CustomAssertions.singleListAssertThat(underTest.getQueue(gameData, LOCATION))
            .returns(DECONSTRUCTION_ID, QueueItem::getItemId)
            .returns(QueueItemType.DECONSTRUCT_CONSTRUCTION_AREA, QueueItem::getType)
            .returns(REQUIRED_WORK_POINTS, QueueItem::getRequiredWorkPoints)
            .returns(CURRENT_WORK_POINTS, QueueItem::getCurrentWorkPoints)
            .returns(PRIORITY, QueueItem::getPriority)
            .returns(Map.of("dataId", DATA_ID), QueueItem::getData);
    }

    @Test
    void setPriority() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(game.getProgressDiff()).willReturn(gameProgressDiff);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(deconstructionConverter.toModel(GAME_ID, deconstruction)).willReturn(deconstructionModel);

        underTest.setPriority(USER_ID, LOCATION, DECONSTRUCTION_ID, PRIORITY);

        then(deconstruction).should().setPriority(PRIORITY);
        then(gameProgressDiff).should().save(deconstructionModel);
    }

    @Test
    void cancel() {
        underTest.cancel(USER_ID, LOCATION, DECONSTRUCTION_ID);

        then(cancelDeconstructionFacade).should().cancelDeconstructionOfConstructionArea(USER_ID, DECONSTRUCTION_ID);
    }
}