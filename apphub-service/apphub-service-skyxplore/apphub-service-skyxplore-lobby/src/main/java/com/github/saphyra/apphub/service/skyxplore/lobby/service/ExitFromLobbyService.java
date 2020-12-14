package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        sendNotification(userId, lobby);
    }

    public void sendDisconnectionMessage(UUID userId) {
        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> sendNotification(userId, lobby));
    }

    private void sendNotification(UUID userId, Lobby lobby) {
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(new ArrayList<>(lobby.getMembers().keySet()))
            .event(WebSocketEvent.builder()
                .eventName(WebSocketEventName.SKYXPLORE_LOBBY_EXIT_FROM_LOBBY)
                .payload(new ExitMessage(
                    userId,
                    lobby.getHost().equals(userId),
                    characterProxy.getCharacter(userId).getName())
                )
                .build())
            .build();
        List<UUID> disconnectedUsers = messageSenderProxy.sendToLobby(message); //TODO handle disconnected users
    }

    @Data
    @AllArgsConstructor
    private static class ExitMessage {
        private UUID userId;
        private boolean host;
        private String characterName;
    }
}
