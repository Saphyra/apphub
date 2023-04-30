package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameDeletionService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreSavedGameControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private GameDeletionService gameDeletionService;

    @Mock
    private GameViewForLobbyCreationQueryService gameViewForLobbyCreationQueryService;

    @Mock
    private DeleteGameItemService deleteGameItemService;

    @Mock
    private SaveGameItemService saveGameItemService;

    @Mock
    private SavedGameQueryService savedGameQueryService;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private SkyXploreSavedGameControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private GameViewForLobbyCreation gameViewForLobbyCreation;

    @Mock
    private SavedGameResponse savedGameResponse;

    @Mock
    private GameModel gameModel;

    @Test
    public void deleteGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.deleteGame(GAME_ID, accessTokenHeader);

        verify(gameDeletionService).deleteByGameId(GAME_ID, USER_ID);
    }

    @Test
    public void getGameViewForLobbyCreation() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(gameViewForLobbyCreationQueryService.getView(USER_ID, GAME_ID)).willReturn(gameViewForLobbyCreation);

        GameViewForLobbyCreation result = underTest.getGameForLobbyCreation(GAME_ID, accessTokenHeader);

        assertThat(result).isEqualTo(gameViewForLobbyCreation);
    }

    @Test
    public void deleteItem() {
        underTest.deleteGameItem(List.of(new BiWrapper<>(ID, GameItemType.PLAYER)));

        verify(deleteGameItemService).deleteItem(ID, GameItemType.PLAYER);
    }

    @Test
    public void saveGameData() {
        underTest.saveGameData(Arrays.asList(ID));

        verify(saveGameItemService).save(Arrays.asList(ID));
    }

    @Test
    public void getSavedGames() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(savedGameQueryService.getSavedGames(USER_ID)).willReturn(Arrays.asList(savedGameResponse));

        List<SavedGameResponse> result = underTest.getSavedGames(accessTokenHeader);

        assertThat(result).containsExactly(savedGameResponse);
    }

    @Test
    void getGameModel_found() {
        given(gameDao.findById(GAME_ID)).willReturn(Optional.of(gameModel));

        assertThat(underTest.getGameModel(GAME_ID)).isEqualTo(gameModel);
    }

    @Test
    void getGameModel_notFound() {
        given(gameDao.findById(GAME_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.getGameModel(GAME_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}