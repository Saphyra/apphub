package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameWebSocketEventApiClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
//TODO unit test
public class SkyXploreGameConnectionHandler extends DefaultWebSocketHandler {
    private final SkyXploreGameWebSocketEventApiClient gameClient;
    private final CommonConfigProperties properties;

    public SkyXploreGameConnectionHandler(WebSocketHandlerContext context, SkyXploreGameWebSocketEventApiClient gameClient, CommonConfigProperties properties) {
        super(context);
        this.gameClient = gameClient;
        this.properties = properties;
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.SKYXPLORE_GAME;
    }

    @Override
    protected void afterConnection(UUID userId) {
        gameClient.userJoinedToGame(userId, properties.getDefaultLocale());
    }

    @Override
    protected void afterDisconnection(UUID userId) {
        gameClient.userLeftGame(userId, properties.getDefaultLocale());
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        log.info("Event from {}: {}", userId, event.getEventName());
        gameClient.processWebSocketEvent(userId, event, properties.getDefaultLocale());
    }

    @Override
    public void handleExpiredConnections(List<UUID> disconnectedUsers) {
        disconnectedUsers.forEach(this::sendExitFromLobbyRequest);
    }

    private void sendExitFromLobbyRequest(UUID userId) {
        try {
            gameClient.userLeftGame(userId, properties.getDefaultLocale());
        } catch (Exception e) {
            log.error("Failed exiting user {} from game.", userId, e);
        }
    }
}
