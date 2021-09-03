package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameDeletionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private GameDeletionService gameDeletionService;

    @Mock
    private GameViewForLobbyCreationQueryService gameViewForLobbyCreationQueryService;

    @Mock
    private LoadGameItemService loadGameItemService;

    @Mock
    private DeleteGameItemService deleteGameItemService;

    @Mock
    private SaveGameItemService saveGameItemService;

    @Mock
    private SavedGameQueryService savedGameQueryService;

    @InjectMocks
    private SkyXploreSavedGameControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private GameModel gameModel;

    @Mock
    private GameViewForLobbyCreation gameViewForLobbyCreation;

    @Mock
    private SavedGameResponse savedGameResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
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

    @Test
    public void saveGameData() {
        underTest.saveGameData(Arrays.asList(ID));

        verify(saveGameItemService).save(Arrays.asList(ID));
    }

    @Test
    public void getSavedGames() {
        given(savedGameQueryService.getSavedGames(USER_ID)).willReturn(Arrays.asList(savedGameResponse));

        List<SavedGameResponse> result = underTest.getSavedGames(accessTokenHeader);

        assertThat(result).containsExactly(savedGameResponse);
    }
}