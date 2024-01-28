package com.github.saphyra.apphub.service.skyxplore.lobby.ws;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect.PlayerDisconnectedService;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler.WebSocketEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SkyXploreLobbyWebSocketHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String SESSION_ID = "session-id";

    @Mock
    private WebSocketHandlerContext context;

    @Mock
    private WebSocketEventHandler eventHandler;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private JoinToLobbyService joinToLobbyService;

    @Mock
    private PlayerDisconnectedService playerDisconnectedService;

    private SkyXploreLobbyWebSocketHandler underTest;

    @Mock
    private WebSocketEvent event;

    @BeforeEach
    void setUp() {
        underTest = SkyXploreLobbyWebSocketHandler.builder()
            .context(context)
            .eventHandlers(List.of(eventHandler))
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getEndpoint() {
        assertThat(underTest.getEndpoint()).isEqualTo(Endpoints.WS_CONNECTION_SKYXPLORE_LOBBY);
    }

    @Test
    void afterConnection() {
        given(applicationContextProxy.getBean(JoinToLobbyService.class)).willReturn(joinToLobbyService);

        underTest.afterConnection(USER_ID, SESSION_ID);

        then(joinToLobbyService).should().userJoinedToLobby(USER_ID);
    }

    @Test
    void afterDisconnection() {
        given(applicationContextProxy.getBean(PlayerDisconnectedService.class)).willReturn(playerDisconnectedService);

        underTest.afterDisconnection(USER_ID, SESSION_ID);

        then(playerDisconnectedService).should().playerDisconnected(USER_ID);
    }

    @Test
    void handleMessage() {
        given(eventHandler.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED)).willReturn(true);
        given(event.getEventName()).willReturn(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED);

        underTest.handleMessage(USER_ID, event, SESSION_ID);

        then(eventHandler).should().handle(USER_ID, event, underTest);
    }
}