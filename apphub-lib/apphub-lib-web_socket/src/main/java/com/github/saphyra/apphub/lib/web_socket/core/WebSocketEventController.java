package com.github.saphyra.apphub.lib.web_socket.core;

import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventController {
    private final List<AbstractWebSocketHandler> webSocketHandlers;

    @PostMapping(GenericEndpoints.EVENT_WEB_SOCKET_SEND_PING_EVENT)
    void sendPing(){
        log.info("Sending ping to WebSocket connections...");
        webSocketHandlers.forEach(AbstractWebSocketHandler::sendPingRequest);
    }

    @PostMapping(GenericEndpoints.EVENT_WEB_SOCKET_CONNECTION_CLEANUP)
    void connectionCleanup(){
        log.info("Cleaning up abandoned WebSocket connections...");
        webSocketHandlers.forEach(AbstractWebSocketHandler::cleanUp);
    }
}
