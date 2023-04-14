package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TerraformationQueueItemPriorityUpdateServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 5;
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private SyncCacheFactory syncCacheFactory;
    @InjectMocks
    private TerraformationQueueItemPriorityUpdateService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surface surface;

    @Mock
    private Construction terraformation;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Constructions constructions;

    @Mock
    private Surfaces surfaces;

    @Mock
    private SyncCache syncCache;

    @Test
    public void priorityTooLow() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, CONSTRUCTION_ID, 0));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too low");
    }

    @Test
    public void priorityTooHigh() {
        Throwable ex = catchThrowable(() -> underTest.updatePriority(USER_ID, PLANET_ID, CONSTRUCTION_ID, 11));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too high");
    }

    @Test
    public void updatePriority() {
        given(syncCacheFactory.create(game)).willReturn(syncCache);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(terraformation.getExternalReference()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(), eq(syncCache))).willReturn(executionResult);

        underTest.updatePriority(USER_ID, PLANET_ID, CONSTRUCTION_ID, PRIORITY);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture(), eq(syncCache));
        argumentCaptor.getValue()
            .run();

        verify(terraformation).setPriority(PRIORITY);
        verify(syncCache).terraformationModified(USER_ID, PLANET_ID, terraformation, surface);
        verify(executionResult).getOrThrow();
    }
}