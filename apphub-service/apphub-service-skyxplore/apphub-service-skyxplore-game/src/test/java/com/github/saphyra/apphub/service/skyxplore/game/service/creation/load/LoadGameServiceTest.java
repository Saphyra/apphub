package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.GameLoader;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoadGameServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();

    @Mock
    private GameLoader gameLoader;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private ExecutorServiceBeanFactory executorServiceBeanFactory;

    @InjectMocks
    private LoadGameService underTest;

    @Mock
    private SkyXploreLoadGameRequest request;

    @Mock
    private GameModel gameModel;

    @BeforeEach
    void setUp() {
        given(executorServiceBeanFactory.create(any())).willReturn(executorServiceBean);

        underTest = LoadGameService.builder()
            .gameLoader(gameLoader)
            .gameDataProxy(gameDataProxy)
            .executorServiceBeanFactory(executorServiceBeanFactory)
            .build();
    }

    @Test
    void loadGame_notHost() {
        given(request.getGameId()).willReturn(GAME_ID);
        given(gameDataProxy.getGameModel(GAME_ID)).willReturn(gameModel);
        given(gameModel.getHost()).willReturn(UUID.randomUUID());
        given(request.getHost()).willReturn(HOST);

        Throwable ex = catchThrowable(() -> underTest.loadGame(request));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    void loadGame() {
        given(request.getGameId()).willReturn(GAME_ID);
        given(gameDataProxy.getGameModel(GAME_ID)).willReturn(gameModel);
        given(gameModel.getHost()).willReturn(HOST);
        given(request.getHost()).willReturn(HOST);
        given(request.getPlayers()).willReturn(List.of(MEMBER_ID));

        underTest.loadGame(request);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceBean).execute(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(gameLoader).loadGame(gameModel, List.of(MEMBER_ID));
    }
}