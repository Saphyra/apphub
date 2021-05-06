package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyWsController;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.WebSocketEventHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreLobbyWsControllerImpl implements SkyXploreLobbyWsController {
    private final ActiveFriendsService activeFriendsService;
    private final WebSocketEventHandlerService webSocketEventHandlerService;

    @Override
    public void processWebSocketEvent(UUID from, WebSocketEvent event) {
        log.info("Handling event {} from {}", event.getEventName(), from);
        webSocketEventHandlerService.handle(from, event);
    }

    @Override
    public void playerOnline(UUID userId) {
        log.info("{} came online.", userId);
        activeFriendsService.playerOnline(userId);
    }

    @Override
    public void playerOffline(UUID userId) {
        log.info("{} went offline.", userId);
        activeFriendsService.playerOffline(userId);
    }
}
