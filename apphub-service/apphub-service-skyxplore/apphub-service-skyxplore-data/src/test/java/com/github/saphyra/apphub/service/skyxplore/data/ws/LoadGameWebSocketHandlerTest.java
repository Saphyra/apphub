package com.github.saphyra.apphub.service.skyxplore.data.ws;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoadGameWebSocketHandlerTest {
    private static final UUID SESSION_ID = UUID.randomUUID();
    private static final String SERIALIZED_EVENT = "serialized-event";
    private static final String PAYLOAD = "payload";

    @Mock
    private WebSocketSessionCache sessionCache;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private SkyXploreWsEventHandler eventHandler;

    private LoadGameWebSocketHandler underTest;

    @Mock
    private WebSocketSession webSocketSession;

    @Mock
    private SkyXploreWsEvent event;

    @Mock
    private TextMessage message;

    @BeforeEach
    public void setUp() {
        underTest = LoadGameWebSocketHandler.builder()
            .idGenerator(idGenerator)
            .sessionCache(sessionCache)
            .objectMapperWrapper(objectMapperWrapper)
            .eventHandlers(List.of(eventHandler))
            .build();
    }

    @Test
    public void afterConnectionEstablished() {
        given(idGenerator.randomUuid()).willReturn(SESSION_ID);

        underTest.afterConnectionEstablished(webSocketSession);

        verify(sessionCache).put(SESSION_ID, webSocketSession);
    }

    @Test
    public void sendEvent() throws IOException {
        given(objectMapperWrapper.writeValueAsString(event)).willReturn(SERIALIZED_EVENT);

        underTest.sendEvent(webSocketSession, event);

        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    public void sendEvent_failed() throws IOException {
        given(objectMapperWrapper.writeValueAsString(event)).willReturn(SERIALIZED_EVENT);
        doThrow(new RuntimeException()).when(webSocketSession).sendMessage(any());

        Throwable ex = catchThrowable(() -> underTest.sendEvent(webSocketSession, event));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void handleTextMessage() {
        given(message.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.readValue(PAYLOAD, SkyXploreWsEvent.class)).willReturn(event);
        given(event.getEventName()).willReturn(SkyXploreWsEventName.LOAD_GAME_ITEM);
        given(eventHandler.canHandle(SkyXploreWsEventName.LOAD_GAME_ITEM)).willReturn(true);

        underTest.handleTextMessage(webSocketSession, message);

        verify(eventHandler).handle(underTest, webSocketSession, event);
    }
}