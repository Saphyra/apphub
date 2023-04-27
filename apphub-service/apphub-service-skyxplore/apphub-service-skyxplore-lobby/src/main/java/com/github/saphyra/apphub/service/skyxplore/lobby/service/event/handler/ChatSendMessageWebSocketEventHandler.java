package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
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
public class ChatSendMessageWebSocketEventHandler implements WebSocketEventHandler {
    private final LobbyDao lobbyDao;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;
    private final DateTimeUtil dateTimeUtil;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event) {
        log.info("Sending message from {}", from);
        Lobby lobby = lobbyDao.findByUserIdValidated(from);

        String senderName = characterProxy.getCharacter(from)
            .getName();

        Message message = Message.builder()
            .senderId(from)
            .senderName(senderName)
            .message(event.getPayload().toString())
            .createdAt(dateTimeUtil.getCurrentTimeEpochMillis())
            .build();

        messageSenderProxy.sendLobbyChatMessage(message, lobby.getMembers().keySet());
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Message {
        private UUID senderId;
        private String senderName;
        private String message;
        private Long createdAt;
    }
}
