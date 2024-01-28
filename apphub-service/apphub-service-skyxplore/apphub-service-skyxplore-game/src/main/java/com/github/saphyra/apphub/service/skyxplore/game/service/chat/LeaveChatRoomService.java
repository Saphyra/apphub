package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LeaveChatRoomService {
    private final GameDao gameDao;
    private final CharacterProxy characterProxy;
    private final SkyXploreGameMainWebSocketHandler webSocketHandler;

    void leave(UUID userId, String roomId) {
        if (GameConstants.CHAT_ROOM_ALLIANCE.equals(roomId) || GameConstants.CHAT_ROOM_GENERAL.equals(roomId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " tried to leave chatRoom " + roomId);
        }

        List<ChatRoom> rooms = gameDao.findByUserIdValidated(userId)
            .getChat()
            .getRooms();
        ChatRoom chatRoom = rooms
            .stream()
            .filter(room -> room.getId().equals(roomId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "ChatRoom not found for id" + roomId));

        boolean memberRemoved = chatRoom.getMembers()
            .remove(userId);

        if (!memberRemoved) {
            log.info("{} was not a member of chatRoom {}", userId, roomId);
            return;
        }

        if (chatRoom.getMembers().isEmpty()) {
            rooms.remove(chatRoom);
            return;
        }

        webSocketHandler.sendEvent(
            chatRoom.getMembers(),
            WebSocketEventName.SKYXPLORE_GAME_USER_LEFT,
            new SystemMessage(
                roomId,
                characterProxy.getCharacterByUserId(userId).getName(),
                userId
            )
        );
    }
}
