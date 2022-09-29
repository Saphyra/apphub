package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreSavedGameController;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final LoadGameItemService loadGameItemService;
    private final DeleteGameItemService deleteGameItemService;
    private final SaveGameItemService saveGameItemService;
    private final SavedGameQueryService savedGameQueryService;

    @Override
    public void saveGameData(List<Object> items) {
        saveGameItemService.save(items);
    }

    @Override
    public GameItem loadGameItem(UUID id, GameItemType type) {
        log.debug("Loading gameItem for id {} and type {}", id, type);
        GameItem result = loadGameItemService.loadGameItem(id, type);
        log.debug("GameItem found for id {} and type {}: {}", id, type, result);
        return result;
    }

    @Override
    public void deleteGameItem(@RequestBody List<BiWrapper<UUID, GameItemType>> items) {
        items.forEach(biWrapper -> deleteGameItemService.deleteItem(biWrapper.getEntity1(), biWrapper.getEntity2()));
    }

    @Override
    public List<? extends GameItem> loadChildrenOfGameItem(UUID parent, GameItemType type) {
        log.debug("Loading children of gameItem for id {} and type {}", parent, type);
        List<? extends GameItem> result = loadGameItemService.loadChildrenOfGameItem(parent, type);
        log.debug("GameItems found for parent {} and type {}: {}", parent, type, result);
        return result;
    }

    @Override
    public List<SavedGameResponse> getSavedGames(AccessTokenHeader accessTokenHeader) {
        log.info("Querying saved games for {}", accessTokenHeader.getUserId());
        return savedGameQueryService.getSavedGames(accessTokenHeader.getUserId());
    }

    @Override
    public void deleteGame(UUID gameId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete game {}", accessTokenHeader.getUserId(), gameId);
        gameDeletionService.deleteByGameId(gameId, accessTokenHeader.getUserId());
    }

    @Override
    public GameViewForLobbyCreation getGameForLobbyCreation(UUID gameId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query game {} for lobby creation", accessTokenHeader.getUserId(), gameId);
        return gameViewForLobbyCreationQueryService.getView(accessTokenHeader.getUserId(), gameId);
    }
}
