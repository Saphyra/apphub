package com.github.saphyra.apphub.service.admin_panel.monitoring;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.monitoring.MemoryStatusModelFactory;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import com.github.saphyra.apphub.service.admin_panel.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.admin_panel.ws.ConnectedWsClients;
import com.github.saphyra.apphub.service.admin_panel.ws.WebSocketMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringControllerImplTest {
    private static final String SERVICE_NAME = "service-name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private MessageSenderProxy messageSenderProxy;

    private final ConnectedWsClients connectedWsClients = new ConnectedWsClients();

    @Mock
    private WebSocketMessageFactory webSocketMessageFactory;

    @Mock
    private EventGatewayProxy eventGatewayProxy;

    @Mock
    private MemoryStatusModelFactory memoryStatusModelFactory;

    private MonitoringControllerImpl underTest;

    @Mock
    private MemoryStatusModel memoryStatusModel;

    @Mock
    private WebSocketMessage webSocketMessage;

    @Captor
    private ArgumentCaptor<SendEventRequest<Void>> argumentCaptor;

    @Before
    public void setUp() {
        underTest = MonitoringControllerImpl.builder()
            .messageSenderProxy(messageSenderProxy)
            .connectedWsClients(connectedWsClients)
            .webSocketMessageFactory(webSocketMessageFactory)
            .eventGatewayProxy(eventGatewayProxy)
            .memoryStatusModelFactory(memoryStatusModelFactory)
            .serviceName(SERVICE_NAME)
            .build();
    }

    @Test
    public void reportMemoryStatus() {
        connectedWsClients.add(USER_ID);
        given(webSocketMessageFactory.create(connectedWsClients, WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS, memoryStatusModel)).willReturn(webSocketMessage);

        underTest.reportMemoryStatus(memoryStatusModel);

        verify(messageSenderProxy).sendToMonitoring(webSocketMessage);
    }

    @Test
    public void triggerMemoryStatusUpdate_noConnectedClient() {
        underTest.triggerMemoryStatusUpdate();

        verifyNoInteractions(eventGatewayProxy);
    }

    @Test
    public void triggerMemoryStatusUpdate() {
        connectedWsClients.add(USER_ID);
        given(memoryStatusModelFactory.create(SERVICE_NAME)).willReturn(memoryStatusModel);
        given(webSocketMessageFactory.create(connectedWsClients, WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS, memoryStatusModel)).willReturn(webSocketMessage);

        underTest.triggerMemoryStatusUpdate();

        verify(eventGatewayProxy).sendEvent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getEventName()).isEqualTo(EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE);
        verify(messageSenderProxy).sendToMonitoring(webSocketMessage);
    }
}