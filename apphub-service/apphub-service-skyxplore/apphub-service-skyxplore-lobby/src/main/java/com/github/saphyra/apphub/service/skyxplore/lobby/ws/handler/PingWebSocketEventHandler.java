package com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PingWebSocketEventHandler implements WebSocketEventHandler {
    private final LobbyDao lobbyDao;
    private final DateTimeUtil dateTimeUtil;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.PING == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event, SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler) {
        log.info("Handling ping from {}", from);
        lobbyDao.findByUserId(from).ifPresent(lobby -> lobby.setLastAccess(dateTimeUtil.getCurrentDateTime()));
    }
}
