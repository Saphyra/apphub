package com.github.saphyra.apphub.api.skyxplore.data.client;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreDataEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "skyxplore-data-game", url = "${serviceUrls.skyxploreData}")
public interface SkyXploreSavedGameClient {
    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION)
    GameViewForLobbyCreation getGameForLobbyCreation(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_SAVE_GAME_DATA)
    void saveGameData(@RequestBody List<GameItem> items, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_DELETE_GAME_ITEM)
    void deleteGameItem(@RequestBody List<BiWrapper<UUID, GameItemType>> items, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_GET_GAME_MODEL)
    GameModel getGameModel(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}