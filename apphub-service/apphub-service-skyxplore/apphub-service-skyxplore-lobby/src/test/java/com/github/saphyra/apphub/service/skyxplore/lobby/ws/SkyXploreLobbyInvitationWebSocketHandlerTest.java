package com.github.saphyra.apphub.service.skyxplore.lobby.ws;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.UserActiveNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SkyXploreLobbyInvitationWebSocketHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String SESSION_ID = "session-id";

    @Mock
    private WebSocketHandlerContext context;

    @Mock
    private UserActiveNotificationService userActiveNotificationService;

    @InjectMocks
    private SkyXploreLobbyInvitationWebSocketHandler underTest;

    @Test
    void getEndpoint() {
        assertThat(underTest.getEndpoint()).isEqualTo(Endpoints.WS_CONNECTION_SKYXPLORE_LOBBY_INVITATION);
    }

    @Test
    void afterConnection() {
        underTest.afterConnection(USER_ID, SESSION_ID);

        then(userActiveNotificationService).should().userOnline(USER_ID);
    }

    @Test
    void afterDisconnection() {
        underTest.afterDisconnection(USER_ID, SESSION_ID);

        then(userActiveNotificationService).should().userOffline(USER_ID);
    }
}