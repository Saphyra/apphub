package com.github.saphyra.apphub.service.skyxplore.game.service.chat.messaging;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.handler.WebSocketEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ChatMessageWebSocketEventHandler implements WebSocketEventHandler {
    private final GameDao gameDao;
    private final ObjectMapper objectMapper;
    private final CharacterProxy characterProxy;
    private final ChatRoomMemberFilter chatRoomMemberFilter;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event, SkyXploreGameMainWebSocketHandler webSocketHandler) {
        IncomingChatMessage incomingChatMessage = objectMapper.convertValue(event.getPayload(), IncomingChatMessage.class);
        log.info("{} sent a message to room {}", from, incomingChatMessage.getRoom());

        ValidationUtil.maxLength(incomingChatMessage.getMessage(), GameConstants.MAXIMUM_CHAT_MESSAGE_LENGTH, "message");

        Game game = gameDao.findByUserIdValidated(from);

        List<ChatRoom> chatRooms = game.getChat().getRooms();

        List<UUID> recipients = chatRoomMemberFilter.getMembers(from, incomingChatMessage.getRoom(), chatRooms, game.getPlayers());

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

        webSocketHandler.sendEvent(recipients, webSocketEvent);
    }
}
