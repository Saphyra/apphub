package com.github.saphyra.apphub.lib.web_socket.core.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWebSocketHandler extends TextWebSocketHandler {
    protected final Map<String, WebSocketSessionWrapper> sessions = new ConcurrentHashMap<>();

    private final WebSocketHandlerContext context;

    public abstract String getEndpoint();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("{} is connected to {}", session.getPrincipal().getName(), getEndpoint());
        WebSocketSessionWrapper sessionWrapper = WebSocketSessionWrapper.builder()
            .session(session)
            .lastUpdate(context.getDateTimeUtil().getCurrentDateTime())
            .build();
        sessions.put(session.getId(), sessionWrapper);
        afterConnection(getUserId(session.getPrincipal()), session.getId());
    }

    protected void afterConnection(UUID userId, String sessionId) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("User {} disconnected from {}", session.getPrincipal().getName(), getEndpoint());
        sessions.remove(session.getId());
        afterDisconnection(getUserId(session.getPrincipal()), session.getId());
    }

    protected void afterDisconnection(UUID userId, String sessionId) {

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        WebSocketEvent event = new WebSocketEvent();
        UUID userId = null;
        try {
            userId = getUserId(session.getPrincipal());
            String payload = message.getPayload();
            event = context.getObjectMapperWrapper().readValue(payload, WebSocketEvent.class);
            Optional.ofNullable(sessions.get(session.getId()))
                .ifPresent(sessionWrapper -> sessionWrapper.setLastUpdate(context.getDateTimeUtil().getCurrentDateTime()));
            handleMessage(userId, event, session.getId());
        } catch (Exception e) {
            log.error("Failed processing event {} from {} in {}", event.getEventName(), userId, getEndpoint(), e);
        }
    }

    protected void handleMessage(UUID userId, WebSocketEvent event, String sessionId) {
        if (event.getEventName() == WebSocketEventName.PING) {
            log.info("Ping arrived from {} to {}", userId, getEndpoint());
        } else {
            log.info("Unhandled event: {} in: {}", event.getEventName(), getEndpoint());
        }
    }

    protected @Nullable UUID getUserId(Principal principal) {
        return Optional.ofNullable(principal.getName())
            .map(s -> context.getUuidConverter().convertEntity(s))
            .orElse(null);
    }

    protected UUID getUserId(String sessionId) {
        WebSocketSessionWrapper sessionWrapper = sessions.get(sessionId);
        return getUserId(sessionWrapper.getSession().getPrincipal());
    }

    public void sendPingRequest() {
        log.info("Sending ping requests to {}...", getEndpoint());
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.PING)
            .build();
        sessions.values()
            .forEach(webSocketSessionWrapper -> sendEventToSession(webSocketSessionWrapper, event));
    }

    public void cleanUp() {
        log.info("Cleaning up expired webSocketSessions in {}", getEndpoint());
        List<String> expiredSessions = sessions.entrySet()
            .stream()
            .filter(this::isExpired)
            .map(Map.Entry::getKey)
            .toList();

        expiredSessions.forEach(sessions::remove);
    }

    private boolean isExpired(Map.Entry<String, WebSocketSessionWrapper> entry) {
        LocalDateTime expiration = context.getDateTimeUtil()
            .getCurrentDateTime()
            .minusSeconds(context.getWebSocketSessionExpirationSeconds());
        return entry.getValue()
            .getLastUpdate()
            .isBefore(expiration);
    }

    public void sendEvent(List<UUID> recipients, WebSocketEventName eventName) {
        sendEvent(recipients, eventName, null);
    }

    public void sendEvent(UUID recipient, WebSocketEventName eventName, Object payload) {
        sendEvent(List.of(recipient), WebSocketEvent.builder().eventName(eventName).payload(payload).build());
    }

    public void sendEvent(Collection<UUID> recipients, WebSocketEventName eventName, Object payload) {
        sendEvent(recipients, WebSocketEvent.builder().eventName(eventName).payload(payload).build());
    }

    public void sendEvent(UUID recipient, WebSocketEvent event) {
        sendEvent(List.of(recipient), event);
    }

    public void sendEvent(Collection<UUID> recipients, WebSocketEvent event) {
        log.info("Sending {} event in {} to {} number of recipients", event.getEventName(), getEndpoint(), recipients.size());
        log.debug("Recipients: {}", recipients);
        recipients.forEach(userId -> sendEventToUser(userId, event));
    }

    public void sendEventToSession(String sessionId, WebSocketEvent event) {
        sendEventToSession(sessions.get(sessionId), event);
    }

    private void sendEventToUser(UUID recipient, WebSocketEvent event) {
        log.debug("Sending {} event to recipient {} for messageGroup {}", event.getEventName(), recipient, getEndpoint());

        getSessionsByUserId(recipient)
            .forEach(webSocketSessionWrapper -> sendEventToSession(webSocketSessionWrapper, event));
    }

    protected List<WebSocketSessionWrapper> getSessionsByUserId(UUID userId) {
        String userIdString = context.getUuidConverter().convertDomain(userId);

        return sessions.values()
            .stream()
            .filter(webSocketSessionWrapper -> userIdString.equals(webSocketSessionWrapper.getSession().getPrincipal().getName()))
            .collect(Collectors.toList());
    }

    protected void sendEventToSession(WebSocketSessionWrapper sessionWrapper, WebSocketEvent event) {
        WebSocketSession session = sessionWrapper.getSession();
        try {
            TextMessage textMessage = new TextMessage(context.getObjectMapperWrapper().writeValueAsString(event));
            synchronized (session) {
                session.sendMessage(textMessage);
            }
            sessionWrapper.setLastUpdate(context.getDateTimeUtil().getCurrentDateTime());
        } catch (Exception e) {
            log.error("Failed to send {} event to {} in messageGroup {}", event.getEventName(), session.getPrincipal().getName(), getEndpoint(), e);
            context.getErrorReporterService()
                .report(String.format("Failed to send %s event to %s in messageGroup %s", event.getEventName(), session.getPrincipal().getName(), getEndpoint()), e);
            afterConnectionClosed(session, null);
        }
    }
}
