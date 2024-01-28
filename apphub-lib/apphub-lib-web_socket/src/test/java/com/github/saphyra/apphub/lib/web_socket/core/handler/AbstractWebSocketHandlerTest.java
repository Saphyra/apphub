package com.github.saphyra.apphub.lib.web_socket.core.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AbstractWebSocketHandlerTest {
    private static final String ENDPOINT = "endpoint";
    private static final int SESSION_EXPIRATION_SECONDS = 23;
    private static final String AFTER_CONNECTION = "after-connection";
    private static final String AFTER_DISCONNECTION = "after-disconnection";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String SESSION_ID = "session-id";
    private static final String USER_ID_STRING = "user-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String HANDLE_MESSAGE = "handle-message";
    private static final String SERIALIZED_EVENT = "serialized-event";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private MethodInterceptor methodInterceptor;

    private AbstractWebSocketHandler underTest;

    @Mock
    private WebSocketSession session;

    @Mock
    private Principal principal;

    @Mock
    private WebSocketEvent event;

    @BeforeEach
    void setUp() {
        WebSocketHandlerContext context = WebSocketHandlerContext.builder()
            .dateTimeUtil(dateTimeUtil)
            .errorReporterService(errorReporterService)
            .webSocketSessionExpirationSeconds(SESSION_EXPIRATION_SECONDS)
            .objectMapperWrapper(objectMapperWrapper)
            .uuidConverter(uuidConverter)
            .build();

        underTest = new TestWebSocketHandler(context, methodInterceptor);
    }

    @Test
    void afterConnectionEstablished() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(session.getId()).willReturn(SESSION_ID);
        given(session.getPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        underTest.afterConnectionEstablished(session);

        assertThat(underTest.sessions.get(SESSION_ID).getLastUpdate()).isEqualTo(CURRENT_TIME);
        assertThat(underTest.sessions.get(SESSION_ID).getSession()).isEqualTo(session);

        then(methodInterceptor).should().methodCall(AFTER_CONNECTION, USER_ID);
    }

    @Test
    void afterConnectionClosed() {
        underTest.sessions.put(SESSION_ID, WebSocketSessionWrapper.builder().session(session).build());
        given(session.getPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(session.getId()).willReturn(SESSION_ID);

        underTest.afterConnectionClosed(session, null);

        then(methodInterceptor).should().methodCall(AFTER_DISCONNECTION, USER_ID);

        assertThat(underTest.sessions).isEmpty();
    }

    @Test
    void sendPingRequest() throws IOException {
        given(objectMapperWrapper.writeValueAsString(any())).willReturn(SERIALIZED_EVENT);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        WebSocketSessionWrapper sessionWrapper = WebSocketSessionWrapper.builder().session(session).build();
        underTest.sessions.put(SESSION_ID, sessionWrapper);

        underTest.sendPingRequest();

        ArgumentCaptor<WebSocketEvent> argumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(objectMapperWrapper).should().writeValueAsString(argumentCaptor.capture());
        WebSocketEvent event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.PING);

        then(session).should().sendMessage(new TextMessage(SERIALIZED_EVENT));
        assertThat(sessionWrapper.getLastUpdate()).isEqualTo(CURRENT_TIME);
    }

    @Test
    void sendEvent_error() {
        WebSocketSessionWrapper sessionWrapper = WebSocketSessionWrapper.builder().session(session).build();
        underTest.sessions.put(SESSION_ID, sessionWrapper);
        given(session.getPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(session.getId()).willReturn(SESSION_ID);
        RuntimeException ex = new RuntimeException();
        given(objectMapperWrapper.writeValueAsString(event)).willThrow(ex);

        underTest.sendEventToSession(sessionWrapper, event);

        then(methodInterceptor).should().methodCall(AFTER_DISCONNECTION, USER_ID);
        then(errorReporterService).should().report(any(), eq(ex));

        assertThat(underTest.sessions).isEmpty();
    }

    @SneakyThrows
    @Test
    void sendEvent() {
        WebSocketSessionWrapper sessionWrapper = WebSocketSessionWrapper.builder().session(session).build();
        underTest.sessions.put(SESSION_ID, sessionWrapper);
        given(session.getPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn(USER_ID_STRING);
        given(objectMapperWrapper.writeValueAsString(event)).willReturn(SERIALIZED_EVENT);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.sendEvent(USER_ID, event);

        then(session).should().sendMessage(new TextMessage(SERIALIZED_EVENT));
        assertThat(sessionWrapper.getLastUpdate()).isEqualTo(CURRENT_TIME);
    }

    @Test
    void handleMessage() {
        WebSocketSessionWrapper sessionWrapper = WebSocketSessionWrapper.builder().session(session).build();
        underTest.sessions.put(SESSION_ID, sessionWrapper);
        given(session.getPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(objectMapperWrapper.readValue(SERIALIZED_EVENT, WebSocketEvent.class)).willReturn(event);
        TextMessage message = new TextMessage(SERIALIZED_EVENT);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(session.getId()).willReturn(SESSION_ID);

        underTest.handleTextMessage(session, message);

        assertThat(sessionWrapper.getLastUpdate()).isEqualTo(CURRENT_TIME);
        then(methodInterceptor).should().methodCall(HANDLE_MESSAGE, USER_ID, event);
    }

    @Test
    void cleanup_valid() {
        WebSocketSessionWrapper sessionWrapper = WebSocketSessionWrapper.builder()
            .lastUpdate(CURRENT_TIME.minusSeconds(SESSION_EXPIRATION_SECONDS - 1))
            .session(session)
            .build();
        underTest.sessions.put(SESSION_ID, sessionWrapper);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.cleanUp();

        assertThat(underTest.sessions).hasSize(1);
    }

    @Test
    void cleanup_expired() {
        WebSocketSessionWrapper sessionWrapper = WebSocketSessionWrapper.builder()
            .lastUpdate(CURRENT_TIME.minusSeconds(SESSION_EXPIRATION_SECONDS + 1))
            .session(session)
            .build();
        underTest.sessions.put(SESSION_ID, sessionWrapper);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);


        underTest.cleanUp();

        assertThat(underTest.sessions).isEmpty();
    }

    private static class TestWebSocketHandler extends AbstractWebSocketHandler {
        private final MethodInterceptor methodInterceptor;

        public TestWebSocketHandler(WebSocketHandlerContext context, MethodInterceptor methodInterceptor) {
            super(context);
            this.methodInterceptor = methodInterceptor;
        }

        @Override
        public String getEndpoint() {
            return ENDPOINT;
        }

        @Override
        protected void afterConnection(UUID userId, String sessionId) {
            methodInterceptor.methodCall(AFTER_CONNECTION, userId);
        }

        @Override
        protected void afterDisconnection(UUID userId, String sessionId) {
            methodInterceptor.methodCall(AFTER_DISCONNECTION, userId);
        }

        @Override
        protected void handleMessage(UUID userId, WebSocketEvent event, String sessionId) {
            methodInterceptor.methodCall(HANDLE_MESSAGE, userId, event);
        }
    }

    @SuppressWarnings("unused")
    private static class MethodInterceptor {
        public void methodCall(String method, Object... params) {

        }
    }
}