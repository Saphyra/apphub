package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameDeletionService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreSavedGameControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final LocalDateTime LAST_PLAYED = LocalDateTime.now();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private GameItemService gameItemService;

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerDao playerDao;

    @Mock
    private GameDeletionService gameDeletionService;

    @Mock
    private GameViewForLobbyCreationQueryService gameViewForLobbyCreationQueryService;

    @Mock
    private LoadGameItemService loadGameItemService;

    @Mock
    private DeleteGameItemService deleteGameItemService;

    private SkyXploreSavedGameControllerImpl underTest;

    @Mock
    private UniverseModel universeModel;

    @Mock
    private AccessTokenHeader accessTokenHeader;

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

    @Mock
    private GameViewForLobbyCreation gameViewForLobbyCreation;

    @Before
    public void setUp() {
        given(gameItemService.getType()).willReturn(GameItemType.UNIVERSE);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest = new SkyXploreSavedGameControllerImpl(
            Arrays.asList(gameItemService),
            objectMapperWrapper,
            gameDao, playerDao, gameDeletionService,
            gameViewForLobbyCreationQueryService,
            loadGameItemService,
            deleteGameItemService
        );
    }

    @Test
    public void saveGameData() {
        given(objectMapperWrapper.convertValue(universeModel, GameItem.class)).willReturn(universeModel);
        given(universeModel.getType()).willReturn(GameItemType.UNIVERSE);
        given(objectMapperWrapper.convertValue(universeModel, UniverseModel.class)).willReturn(universeModel);

        underTest.saveGameData(Arrays.asList(universeModel));

        verify(gameItemService).save(Arrays.asList(universeModel));
    }

    @Test
    public void getSavedGames() {
        given(gameDao.getByHost(USER_ID)).willReturn(Arrays.asList(gameModel));
        given(gameModel.getHost()).willReturn(USER_ID);

        given(gameModel.getGameId()).willReturn(GAME_ID);
        given(gameModel.getName()).willReturn(GAME_NAME);
        given(gameModel.getLastPlayed()).willReturn(LAST_PLAYED);

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

        List<SavedGameResponse> result = underTest.getSavedGames(accessTokenHeader);

        assertThat(result).hasSize(1);
        SavedGameResponse response = result.get(0);
        assertThat(response.getGameId()).isEqualTo(GAME_ID);
        assertThat(response.getGameName()).isEqualTo(GAME_NAME);
        assertThat(response.getLastPlayed()).isEqualTo(LAST_PLAYED);
        assertThat(response.getPlayers()).isEqualTo("player-name-1, player-name-2");
    }

    @Test
    public void deleteGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.deleteGame(GAME_ID, accessTokenHeader);

        verify(gameDeletionService).deleteByGameId(GAME_ID, USER_ID);
    }

    @Test
    public void getGameViewForLobbyCreation() {
        given(gameViewForLobbyCreationQueryService.getView(USER_ID, GAME_ID)).willReturn(gameViewForLobbyCreation);

        GameViewForLobbyCreation result = underTest.getGameForLobbyCreation(GAME_ID, accessTokenHeader);

        assertThat(result).isEqualTo(gameViewForLobbyCreation);
    }

    @Test
    public void loadGameItem() {
        given(loadGameItemService.loadGameItem(ID, GameItemType.GAME)).willReturn(gameModel);

        GameItem result = underTest.loadGameItem(ID, GameItemType.GAME);

        assertThat(result).isEqualTo(gameModel);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void loadChildrenOfGameItem() {
        List list = Arrays.asList(gameModel);
        given(loadGameItemService.loadChildrenOfGameItem(ID, GameItemType.GAME)).willReturn(list);

        List result = underTest.loadChildrenOfGameItem(ID, GameItemType.GAME);

        assertThat(result).containsExactly(gameModel);
    }

    @Test
    public void deleteItem() {
        underTest.deleteGameItem(ID, GameItemType.PLAYER);

        verify(deleteGameItemService).deleteItem(ID, GameItemType.PLAYER);
    }
}