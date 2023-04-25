package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExitFromLobbyService {
    private final CharacterProxy characterProxy;
    private final LobbyDao lobbyDao;
    private final MessageSenderProxy messageSenderProxy;
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;
    private final DateTimeUtil dateTimeUtil;

    public void exit(UUID userId) {
        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exit(userId, lobby));
    }

    public void exit(UUID userId, Lobby lobby) {
        lobby.getMembers().remove(userId);

        sendNotifications(userId, lobby);

        if (lobby.getHost().equals(userId)) {
            log.info("Host left the lobby. Deleting...");
            lobbyDao.delete(lobby);
        }
    }

    private void sendNotifications(UUID userId, Lobby lobby) {
        rejectInvitations(userId, lobby);
        sendExitMessage(userId, lobby);
    }

    private void rejectInvitations(UUID userId, Lobby lobby) {
        lobby.getInvitations()
            .stream()
            .filter(invitation -> invitation.getInvitorId().equals(userId))
            .forEach(this::rejectInvitation);

        lobby.getInvitations().removeIf(invitation -> invitation.getInvitorId().equals(userId));
    }

    private void rejectInvitation(Invitation invitation) {
        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(
            WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION,
            Arrays.asList(invitation.getCharacterId()),
            invitation.getInvitorId()
        );
        messageSenderProxy.sendToMainMenu(message);
    }

    private void sendExitMessage(UUID userId, Lobby lobby) {
        ExitMessage payload = new ExitMessage(
            userId,
            lobby.getHost().equals(userId),
            characterProxy.getCharacter(userId).getName(),
            lobby.getExpectedPlayers().contains(userId),
            dateTimeUtil.getCurrentTimeEpochMillis()
        );
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_EXIT)
            .payload(payload)
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(new ArrayList<>(lobby.getMembers().keySet()))
            .event(event)
            .build();
        messageSenderProxy.sendToLobby(message);
    }

    public void userDisconnected(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        LobbyMember lobbyMember = lobby.getMembers()
            .get(userId);
        lobbyMember.setStatus(LobbyMemberStatus.DISCONNECTED);

        LobbyMemberResponse response = lobbyMemberToResponseConverter.convertMember(lobbyMember);
        messageSenderProxy.lobbyMemberModified(response, lobby.getMembers().keySet());
        messageSenderProxy.lobbyMemberDisconnected(response, lobby.getMembers().keySet());
    }

    @Data
    @AllArgsConstructor
    static class ExitMessage {
        private UUID userId;
        private boolean host;
        private String characterName;
        private boolean expectedPlayer;
        private Long createdAt;
    }
}
