package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.api.platform.message_sender.server.MessageSenderEventController;
import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageSenderEventControllerImpl implements MessageSenderEventController {
    private final List<WebSocketHandler> webSocketHandlers;


    @Override
    //TODO unit test
    //TODO int test
    public void sendPingRequests() {
        log.info("Processing pingRequests event...");
        webSocketHandlers.forEach(WebSocketHandler::sendPingRequest);
    }

    @Override
    //TODO unit test
    //TODO int test
    public void connectionCleanup() {
        log.info("Processing connectionCleanup event...");
        webSocketHandlers.forEach(WebSocketHandler::cleanUp);
    }
}
