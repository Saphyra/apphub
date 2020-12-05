package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Slf4j
abstract class DefaultWebSocketHandler extends TextWebSocketHandler implements WebSocketHandler {
    private final Map<UUID, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private final WebSocketHandlerContext context;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID userId = getUserId(session);
        log.info("{} is connected ", userId);
        sessionMap.put(userId, session);
        afterConnection(userId);
    }

    protected void afterConnection(UUID userId) {

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        WebSocketEvent event = new WebSocketEvent();
        UUID userId = null;
        try {
            userId = getUserId(session);
            String payload = message.getPayload();
            event = context.getObjectMapperWrapper().readValue(payload, WebSocketEvent.class);
            handleMessage(userId, event);
        } catch (Exception e) {
            log.error("Failed processing event {} from {}", event.getEventName(), userId, e);
        }
    }

    @Override
    public abstract MessageGroup getGroup();

    protected abstract void handleMessage(UUID userId, WebSocketEvent event);

    @Override
    public List<UUID> sendEvent(WebSocketMessage message) {
        return message.getRecipients()
            .stream()
            .map(recipient -> sendEvent(recipient, message.getEvent()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<UUID> sendEvent(UUID recipient, WebSocketEvent event) {
        log.info("Sending {} event to recipient {}", event.getEventName(), recipient);
        try {
            WebSocketSession session = sessionMap.get(recipient);
            if (isNull(session)) {
                throw new RuntimeException(recipient + " is not connected to " + getGroup());
            }
            TextMessage textMessage = new TextMessage(context.getObjectMapperWrapper().writeValueAsString(event));
            session.sendMessage(textMessage);
            return Optional.empty();
        } catch (Exception e) {
            log.info("Failed to send {} event to {}", event.getPayload(), recipient, e);
            sessionMap.remove(recipient);
            return Optional.of(recipient);
        }
    }

    private UUID getUserId(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        if (isNull(principal)) {
            throw new IllegalArgumentException("Principal is not set.");
        }
        return context.getUuidConverter().convertEntity(principal.getName());
    }
}
