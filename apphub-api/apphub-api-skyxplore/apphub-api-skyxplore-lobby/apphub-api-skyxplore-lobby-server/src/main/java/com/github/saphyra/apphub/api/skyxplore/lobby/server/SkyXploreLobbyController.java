package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXploreLobbyController {
    @GetMapping(Endpoints.SKYXPLORE_LOBBY_IS_IN_LOBBY)
    OneParamResponse<Boolean> isUserInLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Creating a new lobby with the given game name
     */
    @PutMapping(Endpoints.SKYXPLORE_CREATE_LOBBY)
    void createLobby(@RequestBody OneParamRequest<String> name, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Checking if the given user is already in a lobby, and returning the details of that lobby.
     * Used by WebUI to redirect the user, or display the lobby page.
     */
    @GetMapping(Endpoints.SKYXPLORE_LOBBY_VIEW_FOR_PAGE)
    LobbyViewForPage lobbyForPage(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Removing the user from the lobby. Also deleting the lobby if the host left.
     */
    @DeleteMapping(Endpoints.SKYXPLORE_EXIT_FROM_LOBBY)
    void exitFromLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Inviting the given friend to the lobby.
     * Rate limited
     */
    @PostMapping(Endpoints.SKYXPLORE_INVITE_TO_LOBBY)
    void inviteToLobby(@PathVariable("friendId") UUID friendId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION)
    void acceptInvitation(@PathVariable("invitorId") UUID invitorId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_GET_PLAYERS)
    List<LobbyPlayerResponse> getPlayersOfLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Starting the game creation / loading depending on the lobby was created as brand-new, or from a saved game.
     */
    @PostMapping(Endpoints.SKYXPLORE_LOBBY_START_GAME)
    void startGame(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS)
    List<ActiveFriendResponse> getActiveFriends(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Creating a lobby from a saved game.
     * Automatically invites the players of the given game
     */
    @PostMapping(Endpoints.SKYXPLORE_LOBBY_LOAD_GAME)
    void loadGame(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Callback for game service to notify the lobby members about the game is loaded.
     */
    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_GAME_LOADED)
    void gameLoaded(@PathVariable("gameId") UUID gameId);
}
