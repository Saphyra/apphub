package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.server.MessageSenderController;
import com.github.saphyra.apphub.lib.common_util.map.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.map.OptionalMap;
import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MessageSenderControllerImpl implements MessageSenderController {
    private final OptionalMap<MessageGroup, WebSocketHandler> connectionGroups;

    public MessageSenderControllerImpl(List<WebSocketHandler> webSocketHandlers) {
        this.connectionGroups = new OptionalHashMap<>(webSocketHandlers.stream()
            .collect(Collectors.toMap(WebSocketHandler::getGroup, Function.identity())));
    }

    @Override
    //TODO unit test
    //TODO int test
    public List<UUID> sendMessage(MessageGroup group, WebSocketMessage message) {
        WebSocketHandler webSocketHandler = connectionGroups.getOptional(group)
            .orElseThrow(() -> new RuntimeException("ConnectionGroup not found for MessageGroup " + group)); //TODO proper exception

        return webSocketHandler.sendEvent(message);
    }
}