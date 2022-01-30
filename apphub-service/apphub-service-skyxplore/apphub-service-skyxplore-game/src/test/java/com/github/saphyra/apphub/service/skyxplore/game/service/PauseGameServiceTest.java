package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WebSocketMessageFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PauseGameServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private WebSocketMessageFactory messageFactory;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private PauseGameService underTest;

    @Mock
    private Game game;

    @Mock
    private WebSocketMessage message;

    @Test
    public void nullPaused() {
        Throwable ex = catchThrowable(() -> underTest.setPausedStatus(USER_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "isPaused", "must not be null");
    }

    @Test
    public void setPausedStatus() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getConnectedPlayers()).willReturn(List.of(USER_ID));
        given(messageFactory.create(List.of(USER_ID), WebSocketEventName.SKYXPLORE_GAME_PAUSED, true)).willReturn(message);

        underTest.setPausedStatus(USER_ID, true);

        verify(game).setGamePaused(true);
        verify(messageSenderProxy).sendToGame(message);
    }
}