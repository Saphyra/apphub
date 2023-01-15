package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.admin_panel.client.AdminPanelWsClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminPanelMonitoringWebSocketHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private WebSocketHandlerContext context;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private AdminPanelWsClient adminPanelWsClient;

    @InjectMocks
    private AdminPanelMonitoringWebSocketHandler underTest;

    @Mock
    private WebSocketEvent event;

    @BeforeEach
    public void setUp() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
    }

    @Test
    public void getGroup() {
        assertThat(underTest.getGroup()).isEqualTo(MessageGroup.ADMIN_PANEL_MONITORING);
    }

    @Test
    public void afterConnection() {
        underTest.afterConnection(USER_ID);

        verify(adminPanelWsClient).userConnected(USER_ID, LOCALE);
    }

    @Test
    public void afterDisconnection() {
        underTest.afterDisconnection(USER_ID);

        verify(adminPanelWsClient).userDisconnected(USER_ID, LOCALE);
    }

    @Test
    public void handleMessage() {
        underTest.handleMessage(USER_ID, event);

        verify(adminPanelWsClient).processWebSocketEvent(USER_ID, event, LOCALE);
    }

    @Test
    public void handleExpiredConnections() {
        underTest.handleExpiredConnections(List.of(USER_ID));

        verify(adminPanelWsClient).userDisconnected(USER_ID, LOCALE);
    }
}