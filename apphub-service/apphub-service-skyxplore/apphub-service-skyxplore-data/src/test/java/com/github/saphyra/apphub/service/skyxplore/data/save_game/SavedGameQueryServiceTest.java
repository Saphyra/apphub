package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SavedGameQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final LocalDateTime LAST_PLAYED = LocalDateTime.now();
    private static final long LAST_PLAYED_EPOCH_SECONDS = 134L;

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerDao playerDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private SavedGameQueryService underTest;

    @Mock
    private GameModel gameModel;

    @Mock
    private PlayerModel player1;

    @Mock
    private PlayerModel player2;

    @Mock
    private PlayerModel hostPlayer;

    @Mock
    private PlayerModel aiPlayer;

    @Test
    public void getSavedGames_markedForDeletion() {
        given(gameDao.getByHost(USER_ID)).willReturn(Arrays.asList(gameModel));
        given(gameModel.getMarkedForDeletion()).willReturn(false);

        List<SavedGameResponse> result = underTest.getSavedGames(USER_ID);

        assertThat(result.isEmpty());
    }

    @Test
    public void getSavedGames() {
        given(gameDao.getByHost(USER_ID)).willReturn(Arrays.asList(gameModel));
        given(gameModel.getHost()).willReturn(USER_ID);
        given(gameModel.getMarkedForDeletion()).willReturn(false);

        given(gameModel.getGameId()).willReturn(GAME_ID);
        given(gameModel.getName()).willReturn(GAME_NAME);
        given(gameModel.getLastPlayed()).willReturn(LAST_PLAYED);
        given(dateTimeUtil.toEpochSecond(LAST_PLAYED)).willReturn(LAST_PLAYED_EPOCH_SECONDS);

        given(playerDao.getByGameId(GAME_ID)).willReturn(Arrays.asList(player2, player1, hostPlayer, aiPlayer));
        given(player1.getAi()).willReturn(false);
        given(player1.getUserId()).willReturn(UUID.randomUUID());
        given(player1.getUsername()).willReturn("player-name-1");

        given(player2.getAi()).willReturn(false);
        given(player2.getUserId()).willReturn(UUID.randomUUID());
        given(player2.getUsername()).willReturn("player-name-2");

        given(aiPlayer.getAi()).willReturn(true);

        given(hostPlayer.getAi()).willReturn(false);
        given(hostPlayer.getUserId()).willReturn(USER_ID);

        List<SavedGameResponse> result = underTest.getSavedGames(USER_ID);

        assertThat(result).hasSize(1);
        SavedGameResponse response = result.get(0);
        assertThat(response.getGameId()).isEqualTo(GAME_ID);
        assertThat(response.getGameName()).isEqualTo(GAME_NAME);
        assertThat(response.getLastPlayed()).isEqualTo(LAST_PLAYED_EPOCH_SECONDS);
        assertThat(response.getPlayers()).isEqualTo("player-name-1, player-name-2");
    }
}