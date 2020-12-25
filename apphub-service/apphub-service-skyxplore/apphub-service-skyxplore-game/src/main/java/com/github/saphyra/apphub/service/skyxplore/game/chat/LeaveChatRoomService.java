package com.github.saphyra.apphub.service.skyxplore.game.chat;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.controller.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LeaveChatRoomService {
    private final GameDao gameDao;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;

    void leave(UUID userId, String roomId) {
        if (GameConstants.CHAT_ROOM_ALLIANCE.equals(roomId) || GameConstants.CHAT_ROOM_GENERAL.equals(roomId)) {
            throw new RuntimeException(userId + " cannot leave room " + roomId); //TODO proper exception
        }

        List<ChatRoom> rooms = gameDao.findByUserIdValidated(userId)
            .getChat()
            .getRooms();
        ChatRoom chatRoom = rooms
            .stream()
            .filter(room -> room.getId().equals(roomId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Chat room not found with id " + roomId)); //TODO throw proper exception

        chatRoom.getMembers().remove(userId);

        if (chatRoom.getMembers().isEmpty()) {
            rooms.remove(chatRoom);
            return;
        }

        WebSocketEvent webSocketEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)
            .payload(new SystemMessage(roomId, characterProxy.getCharacterByUserId(userId).getName(), userId))
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(chatRoom.getMembers())
            .event(webSocketEvent)
            .build();
        messageSenderProxy.sendToGame(message);
    }
}
