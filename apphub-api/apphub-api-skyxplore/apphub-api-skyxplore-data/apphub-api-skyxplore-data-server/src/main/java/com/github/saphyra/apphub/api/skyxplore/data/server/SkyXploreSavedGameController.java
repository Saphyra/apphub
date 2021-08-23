package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXploreSavedGameController {
    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_SAVE_GAME_DATA)
    void saveGameData(@RequestBody List<Object> items);

    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_LOAD_GAME_ITEM)
    GameItem loadGameItem(@PathVariable("id") UUID id, @PathVariable("type") GameItemType type);

    @DeleteMapping(Endpoints.SKYXPLORE_INTERNAL_DELETE_GAME_ITEM)
    void deleteGameItem(@PathVariable("id") UUID id, @PathVariable("type") GameItemType type);

    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_LOAD_GAME_ITEM_CHILDREN)
    List<? extends GameItem> loadChildrenOfGameItem(@PathVariable("parent") UUID parent, @PathVariable("type") GameItemType type);

    @GetMapping(Endpoints.SKYXPLORE_GET_GAMES)
    List<SavedGameResponse> getSavedGames(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_DELETE_GAME)
    void deleteGame(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION)
    GameViewForLobbyCreation getGameForLobbyCreation(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}