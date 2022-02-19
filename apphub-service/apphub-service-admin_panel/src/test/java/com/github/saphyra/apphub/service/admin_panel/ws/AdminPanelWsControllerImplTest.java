package com.github.saphyra.apphub.service.admin_panel.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdminPanelWsControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    private final ConnectedWsClients connectedWsClients = new ConnectedWsClients();

    @Mock
    private WebSocketEventHandler webSocketEventHandler;

    private AdminPanelWsControllerImpl underTest;

    @Mock
    private WebSocketEvent webSocketEvent;

    @Before
    public void setUp() {
        underTest = new AdminPanelWsControllerImpl(
            connectedWsClients,
            List.of(webSocketEventHandler)
        );
    }

    @Test
    public void userConnected() {
        underTest.userConnected(USER_ID);
        underTest.userConnected(USER_ID);

        assertThat(connectedWsClients).containsExactly(USER_ID);
    }

    @Test
    public void userDisconnected() {
        underTest.userConnected(USER_ID);

        underTest.userDisconnected(USER_ID);

        assertThat(connectedWsClients).isEmpty();
    }

    @Test
    public void processWebSocketEvent() {
        given(webSocketEvent.getEventName()).willReturn(WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS);
        given(webSocketEventHandler.canHandle(WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS)).willReturn(true);

        underTest.processWebSocketEvent(USER_ID, webSocketEvent);

        verify(webSocketEventHandler).handle(USER_ID, webSocketEvent);
    }
}