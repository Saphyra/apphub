package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameWebSocketEventController;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.query.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreGameWebSocketEventControllerImpl implements SkyXploreGameWebSocketEventController {
    private final GameDao gameDao;
    private final CharacterProxy characterProxy;
    private final List<WebSocketEventHandler> handlers;
    private final MessageSenderProxy messageSenderProxy;

    @Override
    //TODO unit test
    //TODO int test
    public void processWebSocketEvent(UUID from, WebSocketEvent event) {
        log.info("Processing WebSocketEvent {} from {}", event.getEventName(), from);
        List<WebSocketEventHandler> eventHandlers = handlers.stream()
            .filter(webSocketEventHandler -> webSocketEventHandler.canHandle(event.getEventName()))
            .collect(Collectors.toList());

        eventHandlers.forEach(webSocketEventHandler -> webSocketEventHandler.handle(from, event));

        if (eventHandlers.isEmpty()) {
            log.warn("No {} found for event {}", WebSocketEventHandler.class.getName(), event.getEventName());
        }
    }

    @Override
    //TODO unit test
    //TODO int test
    public void userJoinedToGame(UUID userId) {
        log.info("{} joined to the game.", userId);
        Game game = gameDao.findByUserIdValidated(userId);

        game.getPlayers().get(userId).setConnected(true);

        String userName = characterProxy.getCharacterByUserId(userId).getName();

        game.getChat()
            .getRooms()
            .stream()
            .filter(chatRoom -> chatRoom.getMembers().contains(userId))
            .forEach(chatRoom -> sendMessage(
                game.filterConnectedPlayersFrom(chatRoom.getMembers()),
                WebSocketEventName.SKYXPLORE_GAME_USER_JOINED,
                new SystemMessage(chatRoom.getId(), userName, userId)
            ));
    }

    @Override
    //TODO unit test
    //TODO int test
    public void userLeftGame(UUID userId) {
        log.info("{} left the game.", userId);
        Game game = gameDao.findByUserIdValidated(userId);

        game.getPlayers().get(userId).setConnected(false);

        String userName = characterProxy.getCharacterByUserId(userId).getName();

        game.getChat()
            .getRooms()
            .stream()
            .filter(chatRoom -> chatRoom.getMembers().contains(userId))
            .forEach(chatRoom -> sendMessage(
                game.filterConnectedPlayersFrom(chatRoom.getMembers()),
                WebSocketEventName.SKYXPLORE_GAME_USER_LEFT,
                new SystemMessage(chatRoom.getId(), userName, userId)
            ));
    }

    private void sendMessage(List<UUID> recipients, WebSocketEventName eventName, Object payload) {
        WebSocketEvent webSocketEvent = WebSocketEvent.builder()
            .eventName(eventName)
            .payload(payload)
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(recipients)
            .event(webSocketEvent)
            .build();
        messageSenderProxy.sendToGame(message);
    }
}
