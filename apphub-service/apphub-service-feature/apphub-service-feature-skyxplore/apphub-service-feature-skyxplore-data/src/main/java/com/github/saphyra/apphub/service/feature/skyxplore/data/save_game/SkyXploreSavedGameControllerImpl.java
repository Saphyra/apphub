package com.github.saphyra.apphub.service.feature.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.feature.skyxplore.data.server.SkyXploreSavedGameController;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.feature.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.skyxplore.data.save_game.dao.GameDeletionService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.save_game.dao.game.GameDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreSavedGameControllerImpl implements SkyXploreSavedGameController {
    private final GameDeletionService gameDeletionService;
    private final GameViewForLobbyCreationQueryService gameViewForLobbyCreationQueryService;
    private final DeleteGameItemService deleteGameItemService;
    private final SaveGameItemService saveGameItemService;
    private final SavedGameQueryService savedGameQueryService;
    private final GameDao gameDao;

    @Override
    public void saveGameData(List<Object> items) {
        saveGameItemService.save(items);
    }

    @Override
    public void deleteGameItem(@RequestBody List<BiWrapper<UUID, GameItemType>> items) {
        items.forEach(biWrapper -> deleteGameItemService.deleteItem(biWrapper.getEntity1(), biWrapper.getEntity2()));
    }

    @Override
    public List<SavedGameResponse> getSavedGames(AccessToken accessToken) {
        log.info("Querying saved games for {}", accessToken.getUserId());
        return savedGameQueryService.getSavedGames(accessToken.getUserId());
    }

    @Override
    public void deleteGame(UUID gameId, AccessToken accessToken) {
        log.info("{} wants to delete game {}", accessToken.getUserId(), gameId);
        gameDeletionService.deleteByGameId(gameId, accessToken.getUserId());
    }

    @Override
    public GameViewForLobbyCreation getGameForLobbyCreation(UUID gameId, AccessToken accessToken) {
        log.info("{} wants to query game {} for lobby creation", accessToken.getUserId(), gameId);
        return gameViewForLobbyCreationQueryService.getView(accessToken.getUserId(), gameId);
    }

    @Override
    public GameModel getGameModel(UUID gameId) {
        log.info("Querying GameModel by gameId {}", gameId);
        return gameDao.findById(gameId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Game not found by gameId " + gameId));
    }
}
