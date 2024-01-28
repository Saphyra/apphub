package com.github.saphyra.apphub.service.skyxplore.lobby.ws;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.UserActiveNotificationService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SkyXploreLobbyInvitationWebSocketHandler extends AbstractWebSocketHandler {
    private final UserActiveNotificationService userActiveNotificationService;

    public SkyXploreLobbyInvitationWebSocketHandler(WebSocketHandlerContext context, UserActiveNotificationService userActiveNotificationService) {
        super(context);
        this.userActiveNotificationService = userActiveNotificationService;
    }

    @Override
    public String getEndpoint() {
        return Endpoints.WS_CONNECTION_SKYXPLORE_LOBBY_INVITATION;
    }

    @Override
    public void afterConnection(UUID userId, String sessionId) {
        userActiveNotificationService.userOnline(userId);
    }

    @Override
    public void afterDisconnection(UUID userId, String sessionId) {
        userActiveNotificationService.userOffline(userId);
    }

    public boolean isConnected(UUID userId) {
        return !getSessionsByUserId(userId).isEmpty();
    }
}
