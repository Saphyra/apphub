package com.github.saphyra.apphub.service.skyxplore.game.chat;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.controller.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WebSocketEventHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChatMessageWebSocketEventHandler implements WebSocketEventHandler {
    private final GameDao gameDao;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event) {
        IncomingChatMessage incomingChatMessage = objectMapperWrapper.convertValue(event.getPayload(), IncomingChatMessage.class);
        log.info("{} sent a message to room {}", from, incomingChatMessage.getRoom());

        Game game = gameDao.findByUserIdValidated(from);

        List<ChatRoom> chatRooms = game.getChat().getRooms();

        List<UUID> recipients = getMembers(from, incomingChatMessage.getRoom(), chatRooms, game.getPlayers());

        log.info("Recipients for chatMessage from {} in room {}: {}", from, incomingChatMessage.getRoom(), recipients);
        String senderName = characterProxy.getCharacterByUserId(from)
            .getName();

        OutgoingChatMessage outgoingMessage = OutgoingChatMessage.builder()
            .senderId(from)
            .senderName(senderName)
            .room(incomingChatMessage.getRoom())
            .message(incomingChatMessage.getMessage())
            .build();
        WebSocketEvent webSocketEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE)
            .payload(outgoingMessage)
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(recipients)
            .event(webSocketEvent)
            .build();
        messageSenderProxy.sendToGame(message);
    }

    private List<UUID> getMembers(UUID from, String room, List<ChatRoom> chatRooms, Map<UUID, Player> players) {
        if (GameConstants.CHAT_ROOM_GENERAL.equals(room)) {
            return filterConnectedPlayers(players.values());
        }

        if (GameConstants.CHAT_ROOM_ALLIANCE.equals(room)) {
            Optional<ChatRoom> chatRoom = findAllianceRoomOf(from, chatRooms);

            return chatRoom.map(ChatRoom::getMembers)
                .orElse(Arrays.asList(from));
        }

        return findRoom(room, chatRooms)
            .map(chatRoom -> filterConnectedPlayers(chatRoom.getMembers().stream().map(players::get).collect(Collectors.toList())))
            .orElse(Arrays.asList(from));

    }

    private Optional<ChatRoom> findRoom(String room, List<ChatRoom> chatRooms) {
        return chatRooms.stream()
            .filter(chatRoom -> chatRoom.getId().equals(room))
            .findFirst();
    }

    private Optional<ChatRoom> findAllianceRoomOf(UUID from, List<ChatRoom> chatRooms) {
        return chatRooms.stream()
            .filter(chatRoom -> GameConstants.CHAT_ROOM_ALLIANCE.equals(chatRoom.getId()))
            .filter(chatRoom -> chatRoom.getMembers().contains(from))
            .findFirst();
    }

    private List<UUID> filterConnectedPlayers(Collection<Player> players) {
        return players.stream()
            .filter(Player::isConnected)
            .map(Player::getUserId)
            .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    private static class IncomingChatMessage {
        private String room;
        private String message;
    }

    @Data
    @AllArgsConstructor
    @Builder
    private static class OutgoingChatMessage {
        private String room;
        private String message;
        private UUID senderId;
        private String senderName;
    }
}
