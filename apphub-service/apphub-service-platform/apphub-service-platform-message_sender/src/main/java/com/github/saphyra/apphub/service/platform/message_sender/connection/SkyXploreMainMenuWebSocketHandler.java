package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Controller
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class SkyXploreMainMenuWebSocketHandler extends TextWebSocketHandler implements ConnectionGroup {
    private final Map<UUID, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private final ObjectMapperWrapper objectMapperWrapper;
    private final UuidConverter uuidConverter;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        if (isNull(principal)) {
            throw new IllegalArgumentException("Principal is not set.");
        }
        UUID userId = uuidConverter.convertEntity(principal.getName());
        log.info("{} is connected ", userId);
        sessionMap.put(userId, session);
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.SKYXPLORE_MAIN_MENU;
    }

    @Override
    public void sendEvent(UUID id, Object message) {
        WebSocketSession session = sessionMap.get(id);
        if (isNull(session)) {
            throw new RuntimeException(id + " is not connected to " + getGroup());
        }
        TextMessage textMessage = new TextMessage(objectMapperWrapper.writeValueAsString(message));
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            log.info("Failed to send message to {}", id, e);
            sessionMap.remove(id);
        }
    }
}
