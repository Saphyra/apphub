package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyController;
import com.github.saphyra.apphub.api.skyxplore.response.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.GameSettingsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMembersResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.GameSettings;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game.StartGameService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.LobbyCreationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
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
    public void createLobby(OneParamRequest<String> lobbyName, AccessTokenHeader accessTokenHeader) {
        log.info("Creating lobby for user {} if not exists", accessTokenHeader.getUserId());
        lobbyCreationService.create(accessTokenHeader.getUserId(), lobbyName.getValue());
    }

    @Override
    public LobbyViewForPage lobbyForPage(AccessTokenHeader accessTokenHeader) {
        log.info("Checking if user {} is in lobby...", accessTokenHeader.getUserId());

        Optional<Lobby> lobbyOptional = lobbyDao.findByUserId(accessTokenHeader.getUserId());
        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();

            return LobbyViewForPage.builder()
                .inLobby(true)
                .host(lobby.getHost())
                .gameCreationStarted(lobby.isGameCreationStarted())
                .lobbyName(lobby.getLobbyName())
                .build();
        } else {
            return LobbyViewForPage.builder()
                .inLobby(false)
                .build();
        }
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
        exitFromLobbyService.sendDisconnectionMessage(userId);
    }

    @Override
    public LobbyMembersResponse getMembersOfLobby(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the members of his lobby.", accessTokenHeader.getUserId());
        return lobbyMemberQueryService.getMembers(accessTokenHeader.getUserId());
    }

    @Override
    public GameSettingsResponse getGameSettings(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the settings of his lobby.", accessTokenHeader.getUserId());
        Lobby lobby = lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId());
        GameSettings settings = lobby.getSettings();
        return GameSettingsResponse.builder()
            .universeSize(settings.getUniverseSize().name())
            .systemAmount(settings.getSystemAmount().name())
            .systemSize(settings.getSystemSize().name())
            .planetSize(settings.getPlanetSize().name())
            .aiPresence(settings.getAiPresence().name())
            .build();
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
}
