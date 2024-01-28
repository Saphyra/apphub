package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SaveGameServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private GameConverter gameConverter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private SaveGameService underTest;

    @Mock
    private Game game;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private GameModel gameModel;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Test
    void notHost() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.saveGame(USER_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    void saveGame() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getHost()).willReturn(USER_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(gameConverter.convert(game)).willReturn(gameModel);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willReturn(executionResult);

        underTest.saveGame(USER_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        then(eventLoop).should().processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        then(game).should().setLastPlayed(CURRENT_TIME);
        then(progressDiff).should().save(gameModel);
        then(progressDiff).should().process(gameDataProxy);
        then(executionResult).should().getOrThrow();
    }
}