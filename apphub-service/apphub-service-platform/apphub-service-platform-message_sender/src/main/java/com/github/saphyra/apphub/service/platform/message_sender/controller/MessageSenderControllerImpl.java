package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.platform.message_sender.server.MessageSenderController;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MessageSenderControllerImpl implements MessageSenderController {
    private final OptionalMap<MessageGroup, WebSocketHandler> connectionGroups;
    private final ErrorReporterService errorReporterService;

    public MessageSenderControllerImpl(List<WebSocketHandler> webSocketHandlers, ErrorReporterService errorReporterService) {
        this.connectionGroups = new OptionalHashMap<>(webSocketHandlers.stream()
            .collect(Collectors.toMap(WebSocketHandler::getGroup, Function.identity())));
        this.errorReporterService = errorReporterService;
    }

    @Override
    public void sendMessage(MessageGroup group, WebSocketMessage message) {
        connectionGroups.getOptional(group)
            .orElseThrow(() -> ExceptionFactory.reportedException("ConnectionGroup not found for MessageGroup " + group))
            .sendEvent(message);
    }

    @Override
    public void sendMessage(MessageGroup group, List<WebSocketMessage> messages) {
        log.info("Sending {} messages to group {}", messages.size(), group);
        messages.forEach(webSocketMessage -> sendMessageHandled(group, webSocketMessage));
    }

    private void sendMessageHandled(MessageGroup group, WebSocketMessage webSocketMessage) {
        try {
            sendMessage(group, webSocketMessage);
        } catch (Exception e) {
            log.error("Failed sending message {} to group {}", webSocketMessage.getEvent().getEventName(), group);
            errorReporterService.report(String.format("Failed sending message %s to group %s", webSocketMessage.getEvent().getEventName(), group), e);
        }
    }
}
