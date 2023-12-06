package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.player.LobbyPlayerToResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JoinToLobbyService {
    private final LobbyDao lobbyDao;
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;
    private final LobbyPlayerToResponseConverter lobbyPlayerToResponseConverter;

    public void acceptInvitation(UUID userId, UUID invitorId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(invitorId);

        List<Invitation> invitations = lobby.getInvitations();
        Invitation invitation = invitations
            .stream()
            .filter(i -> i.getCharacterId().equals(userId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " is not invited by " + invitorId));

        invitations.remove(invitation);

        LobbyPlayer lobbyPlayer = LobbyPlayer.builder()
            .userId(userId)
            .status(LobbyPlayerStatus.DISCONNECTED)
            .build();
        lobby.getPlayers()
            .put(userId, lobbyPlayer);
    }

    public void userJoinedToLobby(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);
        LobbyPlayer lobbyPlayer = lobby.getPlayers()
            .get(userId);
        lobbyPlayer.setConnected(true);
        lobbyPlayer.setStatus(LobbyPlayerStatus.NOT_READY);

        LobbyPlayerResponse response = lobbyPlayerToResponseConverter.convertPlayer(lobbyPlayer);
        lobbyWebSocketHandler.sendEvent(lobby.getPlayers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, response);
        lobbyWebSocketHandler.sendEvent(lobby.getPlayers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_CONNECTED, response);
    }
}
