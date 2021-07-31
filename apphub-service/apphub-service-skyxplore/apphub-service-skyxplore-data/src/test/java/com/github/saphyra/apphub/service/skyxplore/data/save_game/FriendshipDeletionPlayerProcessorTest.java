package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipDeletionPlayerProcessorTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerDao playerDao;

    @InjectMocks
    private FriendshipDeletionPlayerProcessor underTest;

    @Mock
    private GameModel gameModel;

    @Mock
    private PlayerModel playerModel1;

    @Mock
    private PlayerModel playerModel2;

    @Test
    public void processDeletedFriendship() {
        given(gameDao.getByHost(USER_ID_1)).willReturn(Arrays.asList(gameModel));
        given(gameModel.getId()).willReturn(GAME_ID);
        given(playerDao.getByGameId(GAME_ID)).willReturn(Arrays.asList(playerModel1, playerModel2));
        given(playerModel1.getUserId()).willReturn(USER_ID_2);

        underTest.processDeletedFriendship(USER_ID_1, USER_ID_2);

        verify(playerModel1).setAi(true);
        verify(playerDao).save(playerModel1);
    }
}