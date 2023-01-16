package com.github.saphyra.apphub.api.skyxplore.lobby.client;

import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "skyxplore-lobby", url = "${serviceUrls.skyxploreLobby}")
public interface SkyXploreLobbyApiClient {
    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_LOBBY_VIEW_FOR_PAGE)
    LobbyViewForPage lobbyForPage(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(Endpoints.SKYXPLORE_EXIT_FROM_LOBBY)
    void exitFromLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_USER_JOINED_TO_LOBBY)
    void userJoinedToLobby(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(Endpoints.SKYXPLORE_INTERNAL_USER_LEFT_LOBBY)
    void userLeftLobby(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
