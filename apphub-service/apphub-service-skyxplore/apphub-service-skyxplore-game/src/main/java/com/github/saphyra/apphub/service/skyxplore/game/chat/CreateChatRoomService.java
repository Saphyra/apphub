package com.github.saphyra.apphub.service.skyxplore.game.chat;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.query.MessageSenderProxy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static feign.Util.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CreateChatRoomService {
    private final IdGenerator idGenerator;
    private final GameDao gameDao;
    private final MessageSenderProxy messageSenderProxy;

    void createChatRoom(UUID userId, CreateChatRoomRequest request) {
        if (isNull(request.getMembers())) {
            //TODO throw exception
        }

        if (isBlank(request.getRoomTitle())) {
            //TODO throw exception
        }

        if (request.getRoomTitle().length() > 20) {
            //TODO throw exception
        }

        Game game = gameDao.findByUserIdValidated(userId);

        List<UUID> members = new ArrayList<>();
        members.add(userId);
        members.addAll(request.getMembers());

        ChatRoom chatRoom = ChatRoom.builder()
            .id(idGenerator.generateRandomId())
            .members(members)
            .build();
        game.getChat()
            .getRooms()
            .add(chatRoom);

        ChatRoomCreatedEvent payload = new ChatRoomCreatedEvent(chatRoom.getId(), request.getRoomTitle());

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED)
            .payload(payload)
            .build();

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(members)
            .event(event)
            .build();
        messageSenderProxy.sendToGame(message);
    }

    @Data
    @AllArgsConstructor
    private static class ChatRoomCreatedEvent {
        private String id;
        private String title;
    }
}
