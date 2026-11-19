package com.github.saphyra.apphub.api.feature.skyxplore.data.client;

import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreDataEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.UUID;

public interface SkyXploreSavedGameClient {
    @GetExchange(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION)
    GameViewForLobbyCreation getGameForLobbyCreation(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @PostExchange(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_SAVE_GAME_DATA)
    void saveGameData(@RequestBody List<GameItem> items);

    @DeleteExchange(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_DELETE_GAME_ITEM)
    void deleteGameItem(@RequestBody List<BiWrapper<UUID, GameItemType>> items);

    @GetExchange(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_GET_GAME_MODEL)
    GameModel getGameModel(@PathVariable("gameId") UUID gameId);
}