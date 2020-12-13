package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.response.GameSettingsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMembersResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

//TODO split - extract WebSocket events
public interface SkyXploreLobbyController {
    @PutMapping(Endpoints.SKYXPLORE_CREATE_LOBBY)
    void createLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.INTERNAL_SKYXPLORE_LOBBY_VIEW_FOR_PAGE)
    LobbyViewForPage lobbyForPage(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_EXIT_FROM_LOBBY)
    void exitFromLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_INVITE_TO_LOBBY)
    void inviteToLobby(@PathVariable("friendId") UUID friendId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION)
    void acceptInvitation(@PathVariable("invitorId") UUID invitorId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_LOBBY_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID from, @RequestBody WebSocketEvent event);

    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_USER_JOINED_TO_LOBBY)
    void userJoinedToLobby(@PathVariable("userId") UUID userId);

    @DeleteMapping(Endpoints.INTERNAL_SKYXPLORE_USER_LEFT_LOBBY)
    void userLeftLobby(@PathVariable("userId") UUID userId);

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_GET_MEMBERS)
    LobbyMembersResponse getMembersOfLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_GET_GAME_SETTINGS)
    GameSettingsResponse getGameSettings(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
