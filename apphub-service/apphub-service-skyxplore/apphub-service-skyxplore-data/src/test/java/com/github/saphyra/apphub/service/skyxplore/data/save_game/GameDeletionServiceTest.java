package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.player.PlayerDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String USERNAME = "username";

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerDao playerDao;

    @Mock
    private GameItemService gameItemService;

    private GameDeletionService underTest;

    @Mock
    private PlayerModel playerModel;

    @Mock
    private GameModel gameModel;

    @Before
    public void setUp() {
        underTest = new GameDeletionService(
            Arrays.asList(gameItemService),
            gameDao,
            playerDao
        );
    }

    @Test
    public void deleteByUserId() {
        given(playerDao.getByUserId(USER_ID)).willReturn(Arrays.asList(playerModel));
        given(gameDao.getByHost(USER_ID)).willReturn(Arrays.asList(gameModel));
        given(gameModel.getGameId()).willReturn(GAME_ID);
        given(playerModel.getUsername()).willReturn(USERNAME);

        underTest.deleteByUserId(USER_ID);

        verify(playerModel).setAi(true);
        verify(playerModel).setUsername(USERNAME + " (AI)");
        verify(playerDao).save(playerModel);

        verify(gameItemService).deleteByGameId(GAME_ID);
    }
}