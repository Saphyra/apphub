package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
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

    @GetMapping(Endpoints.SKYXPLORE_GET_GAMES)
    List<SavedGameResponse> getSavedGames(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_DELETE_GAME)
    void deleteGame(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION)
    GameViewForLobbyCreation getGameForLobbyCreation(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
