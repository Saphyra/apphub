package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl.AutoLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameDataLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer UNIVERSE_SIZE = 234;

    @Mock
    private AutoLoader<?, ?> autoLoader;

    @Mock
    private SleepService sleepService;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    private GameDataLoader underTest;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @BeforeEach
    void setUp() {
        underTest = GameDataLoader.builder()
            .loaders(List.of(autoLoader))
            .sleepService(sleepService)
            .executorServiceBean(executorServiceBean)
            .build();
    }

    @Test
    void load() {
        given(executorServiceBean.execute(any())).willReturn(future);
        given(future.isDone())
            .willReturn(false)
            .willReturn(true);

        GameData result = underTest.load(GAME_ID, UNIVERSE_SIZE);

        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getUniverseSize()).isEqualTo(UNIVERSE_SIZE);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceBean).execute(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(sleepService).sleep(100);
        verify(autoLoader).autoLoad(result);
    }
}