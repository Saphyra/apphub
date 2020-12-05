package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
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

import static com.github.saphyra.apphub.service.skyxplore.lobby.service.event.WebSocketEvents.EXIT_FROM_LOBBY;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ExitFromLobbyService {
    private final CharacterProxy characterProxy;
    private final LobbyDao lobbyDao;
    private final MessageSenderProxy messageSenderProxy;

    public void exit(UUID userId) {
        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exit(userId, lobby));
    }

    private void exit(UUID userId, Lobby lobby) {
        lobby.getMembers().remove(userId);

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(lobby.getMembers())
            .event(WebSocketEvent.builder()
                .eventName(EXIT_FROM_LOBBY)
                .payload(new ExitMessage(characterProxy.getCharacter(userId).getName()))
                .build())
            .build();
        List<UUID> disconnectedUsers = messageSenderProxy.sendToLobby(message);

        if (lobby.getHost().equals(userId)) {
            //TODO notify members about the host left the room
        }
    }

    @Data
    @AllArgsConstructor
    private static class ExitMessage {
        private String characterName;
    }
}
