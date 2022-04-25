package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
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
    public void handle(UUID from, WebSocketEvent event) {
        log.info("Handling ping from {}", from);
        lobbyDao.findByUserId(from).ifPresent(lobby -> lobby.setLastAccess(dateTimeUtil.getCurrentTime()));
    }
}
