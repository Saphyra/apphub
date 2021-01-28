package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyWsController;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.WebSocketEventHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreLobbyWsControllerImpl implements SkyXploreLobbyWsController {
    private final WebSocketEventHandlerService webSocketEventHandlerService;

    @Override
    //TODO unit test
    //TODO int test
    public void processWebSocketEvent(UUID from, WebSocketEvent event) {
        log.info("Handling event {} from {}", event.getEventName(), from);
        webSocketEventHandlerService.handle(from, event);
    }
}
