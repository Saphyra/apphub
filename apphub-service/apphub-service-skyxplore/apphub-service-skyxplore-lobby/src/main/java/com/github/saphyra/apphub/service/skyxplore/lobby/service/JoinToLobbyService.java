package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.service.skyxplore.lobby.service.event.WebSocketEvents.JOIN_TO_LOBBY;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class JoinToLobbyService {
    private final LobbyDao lobbyDao;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;

    public void join(UUID userId, UUID invitorId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(invitorId);

        List<Invitation> invitations = lobby.getInvitations();
        Invitation invitation = invitations
            .stream()
            .filter(i -> i.getCharacterId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("You are not invited to this lobby.")); //TODO proper exception

        invitations.remove(invitation);
        lobby.getMembers().add(userId);

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(lobby.getMembers())
            .event(WebSocketEvent.builder()
                .eventName(JOIN_TO_LOBBY)
                .payload(new JoinMessage(characterProxy.getCharacter(userId).getName()))
                .build())
            .build();
        List<UUID> disconnectedUsers = messageSenderProxy.sendToLobby(message);
        //TODO handle disconnectedUsers
    }

    @Data
    @AllArgsConstructor
    private static class JoinMessage {
        private String characterName;
    }
}
