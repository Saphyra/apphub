package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructionQueueServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 6345;

    @Mock
    private GameDao gameDao;

    @Mock
    private BuildingDeconstructionToQueueItemConverter buildingDeconstructionToQueueItemConverter;

    @Mock
    private CancelDeconstructionService cancelDeconstructionService;

    @Mock
    private PriorityValidator priorityValidator;

    @Mock
    private SyncCacheFactory syncCacheFactory;

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
    private Deconstructions deconstructions;

    @Mock
    private SyncCache syncCache;

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
        given(eventLoop.processWithWait(any(), eq(syncCache))).willReturn(executionResult);
        given(syncCacheFactory.create(game)).willReturn(syncCache);

        underTest.setPriority(USER_ID, PLANET_ID, DECONSTRUCTION_ID, PRIORITY);

        verify(priorityValidator).validate(PRIORITY);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(deconstruction).setPriority(PRIORITY);

        verify(executionResult).getOrThrow();
        verify(syncCache).deconstructionModified(USER_ID, PLANET_ID, deconstruction);
    }

    @Test
    void cancel() {
        underTest.cancel(USER_ID, PLANET_ID, DECONSTRUCTION_ID);

        verify(cancelDeconstructionService).cancelDeconstructionOfDeconstruction(USER_ID, PLANET_ID, DECONSTRUCTION_ID);
    }
}