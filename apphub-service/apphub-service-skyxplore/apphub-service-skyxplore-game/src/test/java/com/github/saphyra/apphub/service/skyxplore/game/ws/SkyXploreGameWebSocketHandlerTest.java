package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.game.ws.handler.WebSocketEventHandler;
import com.github.saphyra.apphub.service.skyxplore.game.ws.service.PlayerConnectedService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.service.PlayerDisconnectedService;
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
class SkyXploreGameWebSocketHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private WebSocketEventHandler eventHandler;

    @Mock
    private PlayerConnectedService playerConnectedService;

    @Mock
    private PlayerDisconnectedService playerDisconnectedService;

    @Mock
    private WebSocketHandlerContext context;

    private SkyXploreGameWebSocketHandler underTest;

    @Mock
    private WebSocketEvent event;

    @BeforeEach
    void setUp() {
        underTest = SkyXploreGameWebSocketHandler.builder()
            .handlers(List.of(eventHandler))
            .playerConnectedService(playerConnectedService)
            .playerDisconnectedService(playerDisconnectedService)
            .context(context)
            .build();
    }

    @Test
    void getEndpoint() {
        assertThat(underTest.getEndpoint()).isEqualTo(Endpoints.WS_CONNECTION_SKYXPLORE_GAME);
    }

    @Test
    void handleMessage() {
        given(event.getEventName()).willReturn(WebSocketEventName.PING);
        given(eventHandler.canHandle(WebSocketEventName.PING)).willReturn(true);

        underTest.handleMessage(USER_ID, event);

        then(eventHandler).should().handle(USER_ID, event, underTest);
    }

    @Test
    void afterConnection() {
        underTest.afterConnection(USER_ID);

        then(playerConnectedService).should().playerConnected(USER_ID, underTest);
    }

    @Test
    void afterDisconnection() {
        underTest.afterDisconnection(USER_ID);

        then(playerDisconnectedService).should().playerDisconnected(USER_ID, underTest);
    }
}