package com.github.saphyra.apphub.service.skyxplore.game.ws.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.CommonSkyXploreConfiguration;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
//TODO unit test
//TODO split
public class SkyXploreGameWebSocketHandler extends AbstractWebSocketHandler {
    private final List<WebSocketEventHandler> handlers;
    private final GameDao gameDao;
    private final CharacterProxy characterProxy;
    private final DateTimeUtil dateTimeUtil;
    private final CommonSkyXploreConfiguration configuration;

    public SkyXploreGameWebSocketHandler(WebSocketHandlerContext context, List<WebSocketEventHandler> handlers, GameDao gameDao, CharacterProxy characterProxy, DateTimeUtil dateTimeUtil, CommonSkyXploreConfiguration configuration) {
        super(context);
        this.handlers = handlers;
        this.gameDao = gameDao;
        this.characterProxy = characterProxy;
        this.dateTimeUtil = dateTimeUtil;
        this.configuration = configuration;
    }

    @Override
    public String getEndpoint() {
        return Endpoints.WS_CONNECTION_SKYXPLORE_GAME;
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        log.info("Processing WebSocketEvent {} from {}", event.getEventName(), userId);
        List<WebSocketEventHandler> eventHandlers = handlers.stream()
            .filter(webSocketEventHandler -> webSocketEventHandler.canHandle(event.getEventName()))
            .toList();

        eventHandlers.forEach(webSocketEventHandler -> webSocketEventHandler.handle(userId, event, this));

        if (eventHandlers.isEmpty()) {
            log.warn("No {} found for event {}", WebSocketEventHandler.class.getSimpleName(), event.getEventName());
        }
    }

    @Override
    protected void afterConnection(UUID userId) {
        log.info("{} joined to the game.", userId);
        Game game = gameDao.findByUserIdValidated(userId);

        game.getPlayers()
            .get(userId)
            .setConnected(true);

        sendEvent(userId, WebSocketEventName.SKYXPLORE_GAME_PAUSED, game.isGamePaused());

        String userName = characterProxy.getCharacterByUserId(userId).getName();

        game.getChat()
            .getRooms()
            .stream()
            .filter(chatRoom -> chatRoom.getMembers().contains(userId))
            .forEach(chatRoom -> sendEvent(
                game.filterConnectedPlayersFrom(chatRoom.getMembers()),
                WebSocketEventName.SKYXPLORE_GAME_USER_JOINED,
                new SystemMessage(chatRoom.getId(), userName, userId))
            );

        game.setExpiresAt(null);
    }

    @Override
    public void afterDisconnection(UUID userId) {
        log.info("{} left the game.", userId);
        gameDao.findByUserId(userId).ifPresent(game -> {
            game.getPlayers().get(userId).setConnected(false);

            String userName = characterProxy.getCharacterByUserId(userId).getName();

            if (game.getConnectedPlayers().isEmpty()) {
                game.setExpiresAt(dateTimeUtil.getCurrentDateTime().plusSeconds(configuration.getAbandonedGameExpirationSeconds()));
                return;
            }

            game.setGamePaused(true);
            sendEvent(game.filterConnectedPlayersFrom(game.getPlayers().keySet()), WebSocketEventName.SKYXPLORE_GAME_PAUSED, game.isGamePaused());

            game.getChat()
                .getRooms()
                .stream()
                .filter(chatRoom -> chatRoom.getMembers().contains(userId))
                .forEach(chatRoom -> sendEvent(
                    game.filterConnectedPlayersFrom(chatRoom.getMembers()),
                    WebSocketEventName.SKYXPLORE_GAME_USER_LEFT,
                    new SystemMessage(chatRoom.getId(), userName, userId))
                );
        });
    }
}
