package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyInvitationWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
public class ExitFromLobbyService {
    private final CharacterProxy characterProxy;
    private final LobbyDao lobbyDao;
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;
    private final DateTimeUtil dateTimeUtil;
    private final SkyXploreLobbyInvitationWebSocketHandler invitationWebSocketHandler;
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    public void exit(UUID userId) {
        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exit(userId, lobby));
    }

    public void exit(UUID userId, Lobby lobby) {
        lobby.getMembers()
            .remove(userId);

        sendNotifications(userId, lobby);

        if (lobby.getHost().equals(userId)) {
            log.info("Host left the lobby. Deleting...");
            lobbyDao.delete(lobby);
        }
    }

    private void sendNotifications(UUID userId, Lobby lobby) {
        rejectInvitations(userId, lobby);
        sendExitMessage(userId, lobby, false);
    }

    private void rejectInvitations(UUID userId, Lobby lobby) {
        lobby.getInvitations()
            .stream()
            .filter(invitation -> invitation.getInvitorId().equals(userId))
            .forEach(invitation -> rejectInvitation(invitation, lobby));

        lobby.getInvitations().removeIf(invitation -> invitation.getInvitorId().equals(userId));
    }

    private void rejectInvitation(Invitation invitation, Lobby lobby) {
        invitationWebSocketHandler.sendEvent(invitation.getCharacterId(), WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION, invitation.getInvitorId());
        sendExitMessage(invitation.getCharacterId(), lobby, true);
    }

    private void sendExitMessage(UUID userId, Lobby lobby, boolean onlyInvited) {
        ExitMessage payload = ExitMessage.builder()
            .userId(userId)
            .host(lobby.getHost().equals(userId))
            .characterName(characterProxy.getCharacter(userId).getName())
            .expectedPlayer(lobby.getExpectedPlayers().contains(userId))
            .createdAt(dateTimeUtil.getCurrentTimeEpochMillis())
            .onlyInvited(onlyInvited)
            .build();
        lobbyWebSocketHandler.sendEvent(lobby.getMembers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_EXIT, payload);
    }

    public void userDisconnected(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        LobbyMember lobbyMember = lobby.getMembers()
            .get(userId);
        lobbyMember.setStatus(LobbyMemberStatus.DISCONNECTED);

        LobbyMemberResponse response = lobbyMemberToResponseConverter.convertMember(lobbyMember);
        lobbyWebSocketHandler.sendEvent(lobby.getMembers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, response);
        lobbyWebSocketHandler.sendEvent(lobby.getMembers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED, response);
    }

    @Data
    @AllArgsConstructor
    @Builder
    static class ExitMessage {
        private UUID userId;
        private boolean host;
        private String characterName;
        private boolean expectedPlayer;
        private Long createdAt;
        private boolean onlyInvited;
    }
}
