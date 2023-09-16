package com.github.saphyra.apphub.service.skyxplore.data.ws.load_game;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class LoadGameWebSocketHandler extends TextWebSocketHandler {
    private final IdGenerator idGenerator;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final List<SkyXploreLoadGameWsEventHandler> eventHandlers;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connection established.");
    }

    public void sendEvent(WebSocketSession session, SkyXploreWsEvent event) {
        try {
            String serializedEvent = objectMapperWrapper.writeValueAsString(event);
            TextMessage payload = new TextMessage(serializedEvent);

            synchronized (session) {
                session.sendMessage(payload);
            }
        } catch (Exception e) {
            throw ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, "Failed sending WebSocket message", e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        SkyXploreWsEvent event = objectMapperWrapper.readValue(message.getPayload(), SkyXploreWsEvent.class);

        eventHandlers.stream()
            .filter(eventHandler -> eventHandler.canHandle(event.getEventName()))
            .forEach(eventHandler -> eventHandler.handle(this, session, event));
    }
}
