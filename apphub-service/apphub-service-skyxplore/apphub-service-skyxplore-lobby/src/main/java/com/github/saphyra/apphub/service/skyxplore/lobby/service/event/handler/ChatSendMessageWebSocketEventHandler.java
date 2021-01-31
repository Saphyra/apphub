package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
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
class ChatSendMessageWebSocketEventHandler implements WebSocketEventHandler {
    private final LobbyDao lobbyDao;
    private final SkyXploreCharacterDataApiClient characterClient;
    private final LocaleProvider localeProvider;
    private final MessageSenderApiClient messageSenderClient;

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
        String senderName = characterClient.internalGetCharacterByUserId(from, localeProvider.getLocaleValidated())
            .getName();

        WebSocketEvent webSocketEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)
            .payload(new Message(from, senderName, event.getPayload().toString()))
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(members)
            .event(webSocketEvent)
            .build();
        messageSenderClient.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, localeProvider.getLocaleValidated());
    }

    @Data
    @AllArgsConstructor
    private static class Message {
        private UUID senderId;
        private String senderName;
        private String message;
    }
}