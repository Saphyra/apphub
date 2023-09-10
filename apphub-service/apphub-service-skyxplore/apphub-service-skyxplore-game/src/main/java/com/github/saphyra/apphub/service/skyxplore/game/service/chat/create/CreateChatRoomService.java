package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.ws.handler.SkyXploreGameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateChatRoomService {
    private final ChatRoomFactory chatRoomFactory;
    private final GameDao gameDao;
    private final SkyXploreGameWebSocketHandler webSocketHandler;
    private final CreateChatRoomRequestValidator createChatRoomRequestValidator;

    public void createChatRoom(UUID userId, CreateChatRoomRequest request) {
        Game game = gameDao.findByUserIdValidated(userId);

        createChatRoomRequestValidator.validate(request, game);

        ChatRoom chatRoom = chatRoomFactory.create(userId, request.getMembers());

        game.getChat()
            .addRoom(chatRoom);

        ChatRoomCreatedMessage payload = new ChatRoomCreatedMessage(chatRoom.getId(), request.getRoomTitle());

        webSocketHandler.sendEvent(chatRoom.getMembers(), WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED, payload);
    }
}
