package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreDataEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXploreSavedGameController {
    @PostMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_SAVE_GAME_DATA)
    void saveGameData(@RequestBody List<Object> items);

    @DeleteMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_DELETE_GAME_ITEM)
    void deleteGameItem(@RequestBody List<BiWrapper<UUID, GameItemType>> items);

    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_GET_GAMES)
    List<SavedGameResponse> getSavedGames(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreDataEndpoints.SKYXPLORE_DELETE_GAME)
    void deleteGame(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION)
    GameViewForLobbyCreation getGameForLobbyCreation(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_GET_GAME_MODEL)
    GameModel getGameModel(@PathVariable("gameId") UUID gameId);
}
