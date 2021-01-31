package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.time.LocalDateTime;
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
    @Getter(value = AccessLevel.PACKAGE)
    private final Map<UUID, SessionWrapper> sessionMap = new ConcurrentHashMap<>();

    private final WebSocketHandlerContext context;

    @Override
    public abstract MessageGroup getGroup();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID userId = getUserId(session);
        log.info("{} is connected to messageGroup {}", userId, getGroup());
        SessionWrapper sessionWrapper = SessionWrapper.builder()
            .session(session)
            .lastUpdate(context.getDateTimeUtil().getCurrentDate())
            .build();
        sessionMap.put(userId, sessionWrapper);
        afterConnection(userId);
    }

    protected void afterConnection(UUID userId) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        UUID userId = getUserId(session);
        log.info("User {} disconnected from messageGroup {}", userId, getGroup());
        sessionMap.remove(userId);
        afterDisconnection(userId);
    }

    protected void afterDisconnection(UUID userId) {

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        WebSocketEvent event = new WebSocketEvent();
        UUID userId = null;
        try {
            userId = getUserId(session);
            String payload = message.getPayload();
            event = context.getObjectMapperWrapper().readValue(payload, WebSocketEvent.class);
            sessionMap.get(userId).setLastUpdate(context.getDateTimeUtil().getCurrentDate());
            handleMessage(userId, event);
        } catch (Exception e) {
            log.error("Failed processing event {} from {} in messageGroup {}", event.getEventName(), userId, getGroup(), e);
        }
    }

    protected void handleMessage(UUID userId, WebSocketEvent event) {
        if (event.getEventName() == WebSocketEventName.PING) {
            log.info("Ping arrived from {} to messageGroup {}", userId, getGroup());
        } else {
            log.info("Unhandled event: {} in messageGroup: {}", event.getEventName(), getGroup());
        }
    }

    @Override
    public void sendPingRequest() {
        log.info("Sending ping requests to messageGroup {}...", getGroup());
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.PING)
            .build();
        List<UUID> disconnected = sessionMap.keySet()
            .stream()
            .map(userId -> sendEvent(userId, event))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        handleExpiredConnections(disconnected);
    }

    @Override
    public void cleanUp() {
        log.info("Cleaning up expired webSocketSessions in messageGroup {}", getGroup());
        List<UUID> expiredSessions = sessionMap.entrySet()
            .stream()
            .filter(this::isExpired)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        expiredSessions.forEach(sessionMap::remove);

        handleExpiredConnections(expiredSessions);
    }

    protected void handleExpiredConnections(List<UUID> expiredSessions) {

    }

    private boolean isExpired(Map.Entry<UUID, SessionWrapper> entry) {
        LocalDateTime expiration = context.getDateTimeUtil()
            .getCurrentDate()
            .minusSeconds(context.getWebSocketSessionExpirationSeconds());
        return entry.getValue()
            .getLastUpdate()
            .isBefore(expiration);
    }

    @Override
    public List<UUID> sendEvent(WebSocketMessage message) {
        return message.getRecipients()
            .stream()
            .filter(sessionMap::containsKey)
            .map(recipient -> sendEvent(recipient, message.getEvent()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<UUID> sendEvent(UUID recipient, WebSocketEvent event) {
        log.info("Sending {} event to recipient {} for messageGroup {}", event.getEventName(), recipient, getGroup());
        try {
            SessionWrapper sessionWrapper = sessionMap.get(recipient);
            WebSocketSession session = sessionWrapper.getSession();
            if (isNull(session)) {
                throw new RuntimeException(recipient + " is not connected to " + getGroup());
            }
            TextMessage textMessage = new TextMessage(context.getObjectMapperWrapper().writeValueAsString(event));
            session.sendMessage(textMessage);
            sessionWrapper.setLastUpdate(context.getDateTimeUtil().getCurrentDate());
            return Optional.empty();
        } catch (Exception e) {
            log.info("Failed to send {} event to {} in messageGroup {}", event.getEventName(), recipient, getGroup(), e);
            sessionMap.remove(recipient);
            return Optional.of(recipient);
        }
    }

    private UUID getUserId(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        if (isNull(principal)) {
            throw new IllegalArgumentException("Principal is not set.");
        }
        return context.getUuidConverter()
            .convertEntity(principal.getName());
    }
}
