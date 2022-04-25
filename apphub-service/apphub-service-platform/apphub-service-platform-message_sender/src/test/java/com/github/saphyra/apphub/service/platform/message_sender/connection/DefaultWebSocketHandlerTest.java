package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultWebSocketHandlerTest {
    private static final int WEB_SOCKET_SESSION_EXPIRATION_SECONDS = 342;
    private static final String USER_ID_STRING = "user-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final String MESSAGE_PAYLOAD = "message-payload";

    @Mock
    private TemplateMethods templateMethods;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ErrorReporterService errorReporterService;

    private DefaultWebSocketHandlerImpl underTest;

    @Mock
    private WebSocketSession session;

    @Mock
    private Principal principal;

    @Mock
    private TextMessage textMessage;

    @Mock
    private WebSocketEvent webSocketEvent;

    @Mock
    private SessionWrapper sessionWrapper;

    @Before
    public void setUp() {
        WebSocketHandlerContext context = WebSocketHandlerContext.builder()
            .objectMapperWrapper(objectMapperWrapper)
            .uuidConverter(uuidConverter)
            .dateTimeUtil(dateTimeUtil)
            .webSocketSessionExpirationSeconds(WEB_SOCKET_SESSION_EXPIRATION_SECONDS)
            .errorReporterService(errorReporterService)
            .build();

        underTest = new DefaultWebSocketHandlerImpl(context, templateMethods);

        given(session.getPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_DATE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void afterConnectionEstablished_emptyPrincipal() {
        given(session.getPrincipal()).willReturn(null);

        underTest.afterConnectionEstablished(session);
    }

    @Test
    public void afterConnectionEstablished() throws IOException {
        underTest.getRetryEvents().put(USER_ID, new Vector<>(Arrays.asList(webSocketEvent)));
        given(objectMapperWrapper.writeValueAsString(any())).willReturn(MESSAGE_PAYLOAD);

        underTest.afterConnectionEstablished(session);

        verify(templateMethods).afterConnection(USER_ID);

        assertThat(underTest.getSessionMap()).containsEntry(USER_ID, SessionWrapper.builder().session(session).lastUpdate(CURRENT_DATE).build());
        verify(session, times(1)).sendMessage(any());
    }

    @Test
    public void afterConnectionClosed() {
        underTest.getSessionMap().put(USER_ID, SessionWrapper.builder().build());

        underTest.afterConnectionClosed(session, CloseStatus.NORMAL);

        assertThat(underTest.getSessionMap()).isEmpty();
        verify(templateMethods).afterDisconnection(USER_ID);
    }

    @Test
    public void handleTextMessage() {
        underTest.getSessionMap()
            .put(USER_ID, sessionWrapper);

        given(textMessage.getPayload()).willReturn(MESSAGE_PAYLOAD);
        given(objectMapperWrapper.readValue(MESSAGE_PAYLOAD, WebSocketEvent.class)).willReturn(webSocketEvent);

        underTest.handleTextMessage(session, textMessage);

        verify(sessionWrapper).setLastUpdate(CURRENT_DATE);
        verify(templateMethods).handleMessage(USER_ID, webSocketEvent);
    }

    @Test
    public void sendPingRequest() throws IOException {
        Map<UUID, SessionWrapper> sessionMap = underTest.getSessionMap();
        sessionMap.put(USER_ID, sessionWrapper);
        UUID userId = UUID.randomUUID();
        sessionMap.put(userId, SessionWrapper.builder().build());

        given(objectMapperWrapper.writeValueAsString(any())).willReturn(MESSAGE_PAYLOAD);
        given(sessionWrapper.getSession()).willReturn(session);

        underTest.sendPingRequest();

        ArgumentCaptor<WebSocketEvent> WebSocketEventArgumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        verify(objectMapperWrapper).writeValueAsString(WebSocketEventArgumentCaptor.capture());
        assertThat(WebSocketEventArgumentCaptor.getValue().getEventName()).isEqualTo(WebSocketEventName.PING);

        ArgumentCaptor<TextMessage> textMessageArgumentCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(textMessageArgumentCaptor.capture());
        assertThat(textMessageArgumentCaptor.getValue().getPayload()).isEqualTo(MESSAGE_PAYLOAD);

        verify(sessionWrapper).setLastUpdate(CURRENT_DATE);
    }

    @Test
    public void cleanUp() {
        Map<UUID, SessionWrapper> sessionMap = underTest.getSessionMap();
        sessionMap.put(USER_ID, sessionWrapper);
        UUID userId = UUID.randomUUID();
        sessionMap.put(userId, SessionWrapper.builder().lastUpdate(CURRENT_DATE.minusSeconds(WEB_SOCKET_SESSION_EXPIRATION_SECONDS + 1)).build());

        given(sessionWrapper.getLastUpdate()).willReturn(CURRENT_DATE.minusSeconds(WEB_SOCKET_SESSION_EXPIRATION_SECONDS - 1));

        underTest.cleanUp();

        assertThat(sessionMap).hasSize(1);
        assertThat(sessionMap).containsEntry(USER_ID, sessionWrapper);

        verify(templateMethods).handleExpiredConnections(Arrays.asList(userId));
    }

    @Test
    public void sendEvent() throws IOException {
        Map<UUID, SessionWrapper> sessionMap = underTest.getSessionMap();
        sessionMap.put(USER_ID, sessionWrapper);
        UUID userId = UUID.randomUUID();
        sessionMap.put(userId, SessionWrapper.builder().lastUpdate(CURRENT_DATE.minusSeconds(WEB_SOCKET_SESSION_EXPIRATION_SECONDS + 1)).build());

        given(objectMapperWrapper.writeValueAsString(any())).willReturn(MESSAGE_PAYLOAD);
        given(sessionWrapper.getSession()).willReturn(session);

        WebSocketMessage webSocketMessage = WebSocketMessage.builder()
            .recipients(Arrays.asList(USER_ID, userId))
            .event(webSocketEvent)
            .build();

        underTest.sendEvent(webSocketMessage);

        verify(session, times(1)).sendMessage(any());

        assertThat(underTest.getRetryEvents()).containsKey(userId);
        assertThat(underTest.getRetryEvents().get(userId)).containsExactly(webSocketEvent);
    }

    @Test
    public void sendEvent_error() throws IOException {
        Map<UUID, SessionWrapper> sessionMap = underTest.getSessionMap();
        sessionMap.put(USER_ID, sessionWrapper);
        UUID userId = UUID.randomUUID();
        sessionMap.put(userId, SessionWrapper.builder().lastUpdate(CURRENT_DATE.minusSeconds(WEB_SOCKET_SESSION_EXPIRATION_SECONDS + 1)).build());

        given(objectMapperWrapper.writeValueAsString(any())).willReturn(MESSAGE_PAYLOAD);
        given(sessionWrapper.getSession()).willReturn(session);

        WebSocketMessage webSocketMessage = WebSocketMessage.builder()
            .recipients(Arrays.asList(USER_ID, userId))
            .event(webSocketEvent)
            .build();

        RuntimeException exception = new RuntimeException();
        doThrow(exception).when(session).sendMessage(any());

        underTest.sendEvent(webSocketMessage);

        verify(errorReporterService).report(any(), eq(exception));
    }

    private static class DefaultWebSocketHandlerImpl extends DefaultWebSocketHandler {
        private final TemplateMethods templateMethods;

        public DefaultWebSocketHandlerImpl(WebSocketHandlerContext context, TemplateMethods templateMethods) {
            super(context);
            this.templateMethods = templateMethods;
        }

        @Override
        protected void afterConnection(UUID userId) {
            templateMethods.afterConnection(userId);
        }

        @Override
        protected void afterDisconnection(UUID userId) {
            templateMethods.afterDisconnection(userId);
        }

        @Override
        protected void handleExpiredConnections(List<UUID> expiredSessions) {
            templateMethods.handleExpiredConnections(expiredSessions);
        }

        @Override
        public MessageGroup getGroup() {
            return MessageGroup.SKYXPLORE_LOBBY;
        }

        @Override
        public void handleMessage(UUID userId, WebSocketEvent event) {
            templateMethods.handleMessage(userId, event);
        }
    }

    private interface TemplateMethods {
        void afterConnection(UUID userId);

        void afterDisconnection(UUID userId);

        void handleExpiredConnections(List<UUID> expiredSessions);

        void handleMessage(UUID userId, WebSocketEvent event);
    }
}