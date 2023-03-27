package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.DeconstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructionQueueServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 6345;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private DeconstructionToModelConverter deconstructionToModelConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @Mock
    private BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private CancelDeconstructionService cancelDeconstructionService;

    @Mock
    private PriorityValidator priorityValidator;

    @InjectMocks
    private DeconstructionQueueService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private QueueItem queueItem;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private Deconstructions deconstructions;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(QueueItemType.DECONSTRUCTION);
    }

    @Test
    public void getQueue() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.getByLocation(PLANET_ID)).willReturn(List.of(deconstruction));

        given(buildingDeconstructionToQueueItemConverter.convert(gameData, deconstruction)).willReturn(queueItem);

        List<QueueItem> result = underTest.getQueue(gameData, PLANET_ID);

        assertThat(result).containsExactly(queueItem);
    }

    @Test
    void setPriority() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionId(DECONSTRUCTION_ID)).willReturn(deconstruction);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willReturn(executionResult);
        given(game.getGameId()).willReturn(GAME_ID);
        given(deconstructionToModelConverter.convert(GAME_ID, deconstruction)).willReturn(deconstructionModel);
        given(buildingDeconstructionToQueueItemConverter.convert(gameData, deconstruction)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, gameData, PLANET_ID)).willReturn(queueResponse);

        underTest.setPriority(USER_ID, PLANET_ID, DECONSTRUCTION_ID, PRIORITY);

        verify(priorityValidator).validate(PRIORITY);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(deconstruction).setPriority(PRIORITY);
        verify(gameDataProxy).saveItem(deconstructionModel);
        verify(messageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);

        verify(executionResult).getOrThrow();
    }

    @Test
    void cancel() {
        underTest.cancel(USER_ID, PLANET_ID, DECONSTRUCTION_ID);

        verify(cancelDeconstructionService).cancelDeconstructionOfDeconstruction(USER_ID, PLANET_ID, DECONSTRUCTION_ID);
    }
}