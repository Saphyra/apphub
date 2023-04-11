package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyController;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.LobbyCreationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberQueryService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game.StartGameService;
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
    private final LobbyMemberQueryService lobbyMemberQueryService;
    private final LobbyDao lobbyDao;
    private final StartGameService startGameService;

    @Override
    public OneParamResponse<Boolean> isUserInLobby(AccessTokenHeader accessTokenHeader) {
        boolean isInLobby = lobbyDao.findByUserId(accessTokenHeader.getUserId())
            .isPresent();

        log.info("{} is in lobby: {}", accessTokenHeader.getUserId(), isInLobby);

        return new OneParamResponse<>(isInLobby);
    }

    @Override
    public void createLobby(OneParamRequest<String> lobbyName, AccessTokenHeader accessTokenHeader) {
        log.info("Creating lobby for user {} if not exists", accessTokenHeader.getUserId());
        lobbyCreationService.createNew(accessTokenHeader.getUserId(), lobbyName.getValue());
    }

    @Override
    //TODO unit test
    public LobbyViewForPage lobbyForPage(AccessTokenHeader accessTokenHeader) {
        log.info("Checking if user {} is in lobby...", accessTokenHeader.getUserId());

        Lobby lobby = lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId());
        return LobbyViewForPage.builder()
            .lobbyName(lobby.getLobbyName())
            .isHost(accessTokenHeader.getUserId().equals(lobby.getHost()))
            .lobbyType(lobby.getType().name())
            .ownUserId(accessTokenHeader.getUserId())
            .build();
    }

    @Override
    public void exitFromLobby(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to exit from lobby", accessTokenHeader.getUserId());
        exitFromLobbyService.exit(accessTokenHeader.getUserId());
    }

    @Override
    public void inviteToLobby(UUID friendId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to invite {} to lobby.", accessTokenHeader.getUserId(), friendId);
        invitationService.invite(accessTokenHeader, friendId);
    }

    @Override
    public void acceptInvitation(UUID invitorId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to join to lobby of {}", accessTokenHeader.getUserId(), invitorId);
        joinToLobbyService.acceptInvitation(accessTokenHeader.getUserId(), invitorId);
    }

    @Override
    public void userJoinedToLobby(UUID userId) {
        log.info("User {} is joined to lobby.", userId);
        joinToLobbyService.userJoinedToLobby(userId);
    }

    @Override
    public void userLeftLobby(UUID userId) {
        log.info("User {} is left the lobby", userId);
        exitFromLobbyService.userDisconnected(userId);
    }

    @Override
    public List<LobbyMemberResponse> getMembersOfLobby(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the members of his lobby.", accessTokenHeader.getUserId());
        return lobbyMemberQueryService.getMembers(accessTokenHeader.getUserId());
    }

    @Override
    public void startGame(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to start the game.", accessTokenHeader.getUserId());
        startGameService.startGame(accessTokenHeader.getUserId());
    }

    @Override
    public List<ActiveFriendResponse> getActiveFriends(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his active friends", accessTokenHeader.getUserId());
        return activeFriendsService.getActiveFriends(accessTokenHeader);
    }

    @Override
    public void loadGame(UUID gameId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to load game {}", accessTokenHeader.getUserId(), gameId);
        lobbyCreationService.createForExistingGame(accessTokenHeader.getUserId(), gameId);
    }
}
