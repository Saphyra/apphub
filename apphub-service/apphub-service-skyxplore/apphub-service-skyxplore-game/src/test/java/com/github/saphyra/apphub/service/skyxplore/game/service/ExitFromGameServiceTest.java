package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.ws.SkyXploreGameWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.game.ws.service.PlayerDisconnectedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ExitFromGameServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SkyXploreGameWebSocketHandler webSocketHandler;

    @Mock
    private PlayerDisconnectedService playerDisconnectedService;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private ExitFromGameService underTest;

    @Mock
    private Game game;

    @Test
    public void exitFromGame() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));

        underTest.exitFromGame(USER_ID);

        verify(playerDisconnectedService).playerDisconnected(USER_ID, webSocketHandler);
        verify(gameDao).delete(game);
    }
}