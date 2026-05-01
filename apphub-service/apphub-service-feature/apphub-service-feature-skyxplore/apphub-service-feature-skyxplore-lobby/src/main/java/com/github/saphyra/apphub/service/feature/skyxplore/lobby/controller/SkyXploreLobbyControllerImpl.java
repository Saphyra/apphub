package com.github.saphyra.apphub.service.feature.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.feature.skyxplore.lobby.server.SkyXploreLobbyController;
import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.disconnect.ExitFromLobbyService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.start_game.GameLoadedService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.creation.LobbyCreationService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.invite.InvitationService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.player.LobbyPlayerQueryService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.start_game.StartGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreLobbyControllerImpl implements SkyXploreLobbyController {
    private final ActiveFriendsService activeFriendsService;
    private final ExitFromLobbyService exitFromLobbyService;
    private final LobbyCreationService lobbyCreationService;
    private final InvitationService invitationService;
    private final JoinToLobbyService joinToLobbyService;
    private final LobbyPlayerQueryService lobbyPlayerQueryService;
    private final LobbyDao lobbyDao;
    private final StartGameService startGameService;
    private final GameLoadedService gameLoadedService;

    @Override
    public OneParamResponse<Boolean> isUserInLobby(AccessToken accessToken) {
        boolean isInLobby = lobbyDao.findByUserId(accessToken.getUserId())
            .isPresent();

        log.info("{} is in lobby: {}", accessToken.getUserId(), isInLobby);

        return new OneParamResponse<>(isInLobby);
    }

    @Override
    public void createLobby(OneParamRequest<String> lobbyName, AccessToken accessToken) {
        log.info("Creating lobby for user {} if not exists", accessToken.getUserId());
        lobbyCreationService.createNew(accessToken.getUserId(), lobbyName.getValue());
    }

    @Override
    public LobbyViewForPage lobbyForPage(AccessToken accessToken) {
        log.info("Checking if user {} is in lobby...", accessToken.getUserId());

        Lobby lobby = lobbyDao.findByUserIdValidated(accessToken.getUserId());
        return LobbyViewForPage.builder()
            .lobbyName(lobby.getLobbyName())
            .isHost(accessToken.getUserId().equals(lobby.getHost()))
            .lobbyType(lobby.getType().name())
            .ownUserId(accessToken.getUserId())
            .build();
    }

    @Override
    public void exitFromLobby(AccessToken accessToken) {
        log.info("{} wants to exit from lobby", accessToken.getUserId());
        exitFromLobbyService.exit(accessToken.getUserId());
    }

    @Override
    public void inviteToLobby(UUID friendId, AccessToken accessToken) {
        log.info("{} wants to invite {} to lobby.", accessToken.getUserId(), friendId);
        invitationService.invite(accessToken, friendId);
    }

    @Override
    public void acceptInvitation(UUID invitorId, AccessToken accessToken) {
        log.info("{} wants to join to lobby of {}", accessToken.getUserId(), invitorId);
        joinToLobbyService.acceptInvitation(accessToken.getUserId(), invitorId);
    }

    @Override
    public List<LobbyPlayerResponse> getPlayersOfLobby(AccessToken accessToken) {
        log.info("{} wants to know the players of his lobby.", accessToken.getUserId());
        return lobbyPlayerQueryService.getPlayers(accessToken.getUserId());
    }

    @Override
    public void startGame(AccessToken accessToken) {
        log.info("{} wants to start the game.", accessToken.getUserId());
        startGameService.startGame(accessToken.getUserId());
    }

    @Override
    public List<ActiveFriendResponse> getActiveFriends(AccessToken accessToken) {
        log.info("{} wants to know his active friends", accessToken.getUserId());
        return activeFriendsService.getActiveFriends(accessToken);
    }

    @Override
    public void loadGame(UUID gameId, AccessToken accessToken) {
        log.info("{} wants to load game {}", accessToken.getUserId(), gameId);
        lobbyCreationService.createForExistingGame(accessToken.getUserId(), gameId);
    }

    @Override
    public void gameLoaded(UUID gameId) {
        log.info("Game with id {} is loaded.", gameId);
        gameLoadedService.gameLoaded(gameId);
    }
}
