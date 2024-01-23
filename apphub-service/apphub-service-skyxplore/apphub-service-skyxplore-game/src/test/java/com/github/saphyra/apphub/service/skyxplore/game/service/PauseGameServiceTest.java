package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class PauseGameServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private SkyXploreGameMainWebSocketHandler webSocketHandler;

    @InjectMocks
    private PauseGameService underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Test
    public void nullPaused() {
        Throwable ex = catchThrowable(() -> underTest.setPausedStatus(USER_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "isPaused", "must not be null");
    }

    @Test
    void notHost() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.setPausedStatus(USER_ID, true));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void setPausedStatus() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getConnectedPlayers()).willReturn(List.of(USER_ID));
        given(game.getHost()).willReturn(USER_ID);
        given(game.getPlayers()).willReturn(Map.of(UUID.randomUUID(), player));

        underTest.setPausedStatus(USER_ID, true);

        then(game).should().setGamePaused(true);
        then(player).should().setDisconnectedAt(null);
        then(webSocketHandler).should().sendEvent(List.of(USER_ID), WebSocketEventName.SKYXPLORE_GAME_PAUSED, true);
    }
}