package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

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
class ChatSendMessageWebSocketEventHandler implements WebSocketEventHandler {
    private final LobbyDao lobbyDao;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event) {
        log.info("Sending message from {}", from);
        Lobby lobby = lobbyDao.findByUserIdValidated(from);

        List<UUID> members = new ArrayList<>(lobby.getMembers().keySet());
        log.info("Recipients for chatMessage from {}: {}", from, members);
        String senderName = characterProxy.getCharacter(from)
            .getName();

        WebSocketEvent webSocketEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)
            .payload(new Message(from, senderName, event.getPayload().toString()))
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(members)
            .event(webSocketEvent)
            .build();

        messageSenderProxy.sendToLobby(message);
    }

    @Data
    @AllArgsConstructor
    static class Message {
        private UUID senderId;
        private String senderName;
        private String message;
    }
}
