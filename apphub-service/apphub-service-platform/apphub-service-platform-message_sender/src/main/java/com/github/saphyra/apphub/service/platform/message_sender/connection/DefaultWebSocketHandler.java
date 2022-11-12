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
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
abstract class DefaultWebSocketHandler extends TextWebSocketHandler implements WebSocketHandler {
    @Getter(value = AccessLevel.PACKAGE)
    private final Map<UUID, SessionWrapper> sessionMap = new ConcurrentHashMap<>();

    @Getter(value = AccessLevel.PACKAGE)
    private final Map<UUID, Vector<WebSocketEvent>> retryEvents = new ConcurrentHashMap<>();

    private final WebSocketHandlerContext context;

    @Override
    public abstract MessageGroup getGroup();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID userId = getUserId(session);
        log.info("{} is connected to messageGroup {}", userId, getGroup());
        SessionWrapper sessionWrapper = SessionWrapper.builder()
            .session(session)
            .lastUpdate(context.getDateTimeUtil().getCurrentDateTime())
            .build();
        sessionMap.put(userId, sessionWrapper);
        afterConnection(userId);
        retryEvents(userId);
    }

    protected void afterConnection(UUID userId) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        UUID userId = getUserId(session);
        log.info("User {} disconnected from messageGroup {}", userId, getGroup());
        sessionMap.remove(userId);
        retryEvents.remove(userId);
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
            Optional.ofNullable(sessionMap.get(userId)).ifPresent(sessionWrapper -> sessionWrapper.setLastUpdate(context.getDateTimeUtil().getCurrentDateTime()));
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
        sessionMap.keySet()
            .forEach(userId -> sendEvent(userId, event));
    }

    @Override
    public void cleanUp() {
        log.info("Cleaning up expired webSocketSessions in messageGroup {}", getGroup());
        List<UUID> expiredSessions = sessionMap.entrySet()
            .stream()
            .filter(this::isExpired)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        expiredSessions.forEach(userId -> {
            sessionMap.remove(userId);
            retryEvents.remove(userId);
        });

        handleExpiredConnections(expiredSessions);
    }

    protected void handleExpiredConnections(List<UUID> expiredSessions) {

    }

    private boolean isExpired(Map.Entry<UUID, SessionWrapper> entry) {
        LocalDateTime expiration = context.getDateTimeUtil()
            .getCurrentDateTime()
            .minusSeconds(context.getWebSocketSessionExpirationSeconds());
        return entry.getValue()
            .getLastUpdate()
            .isBefore(expiration);
    }

    @Override
    public void sendEvent(WebSocketMessage message) {
        log.info("Sending {} event in messageGroup {} to {} number of recipients", message.getEvent().getEventName(), getGroup(), message.getRecipients().size());
        log.debug("Recipients: {}", message.getRecipients());
        message.getRecipients()
            .stream()
            .filter(sessionMap::containsKey)
            .filter(recipient -> !sendEvent(recipient, message.getEvent()))
            .forEach(disconnectedRecipient -> addToRetryEvents(disconnectedRecipient, message.getEvent()));
    }

    private boolean sendEvent(UUID recipient, WebSocketEvent event) {
        log.debug("Sending {} event to recipient {} for messageGroup {}", event.getEventName(), recipient, getGroup());
        try {
            SessionWrapper sessionWrapper = sessionMap.get(recipient);
            WebSocketSession session = sessionWrapper.getSession();
            if (isNull(session)) {
                throw new RuntimeException(recipient + " is not connected to " + getGroup());
            }
            TextMessage textMessage = new TextMessage(context.getObjectMapperWrapper().writeValueAsString(event));
            synchronized (session) {
                session.sendMessage(textMessage);
            }
            sessionWrapper.setLastUpdate(context.getDateTimeUtil().getCurrentDateTime());
            return true;
        } catch (Exception e) {
            log.info("Failed to send {} event to {} in messageGroup {}", event.getEventName(), recipient, getGroup(), e);
            context.getErrorReporterService()
                .report(String.format("Failed to send %s event to %s in messageGroup %s", event.getEventName(), recipient, getGroup()), e);
            return false;
        }
    }

    private void addToRetryEvents(UUID recipient, WebSocketEvent event) {
        log.info("Adding event with name {} to retryEvents for recipient {}", event.getEventName(), recipient);
        Vector<WebSocketEvent> list = retryEvents.computeIfAbsent(recipient, r -> new Vector<>());
        list.add(event);
    }

    private void retryEvents(UUID userId) {
        Vector<WebSocketEvent> retryEvents = this.retryEvents.get(userId);
        if (nonNull(retryEvents)) {
            retryEvents.removeIf(event -> sendEvent(userId, event));
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
