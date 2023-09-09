package com.github.saphyra.apphub.service.admin_panel.monitoring;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.monitoring.MemoryStatusModelFactory;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import com.github.saphyra.apphub.service.admin_panel.ws.MemoryMonitoringWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class MonitoringControllerImplTest {
    private static final String SERVICE_NAME = "service-name";

    @Mock
    private MemoryMonitoringWebSocketHandler memoryMonitoringWebSocketHandler;

    @Mock
    private EventGatewayProxy eventGatewayProxy;

    @Mock
    private MemoryStatusModelFactory memoryStatusModelFactory;

    private MonitoringControllerImpl underTest;

    @Mock
    private MemoryStatusModel memoryStatusModel;

    @Captor
    private ArgumentCaptor<SendEventRequest<Void>> sendEventRequestArgumentCaptor;

    @BeforeEach
    public void setUp() {
        underTest = MonitoringControllerImpl.builder()
            .memoryMonitoringWebSocketHandler(memoryMonitoringWebSocketHandler)
            .eventGatewayProxy(eventGatewayProxy)
            .memoryStatusModelFactory(memoryStatusModelFactory)
            .serviceName(SERVICE_NAME)
            .build();
    }

    @Test
    public void reportMemoryStatus() {
        underTest.reportMemoryStatus(memoryStatusModel);

        ArgumentCaptor<WebSocketEvent> argumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(memoryMonitoringWebSocketHandler).should().sendToAllRecipients(argumentCaptor.capture());
        WebSocketEvent event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS);
        assertThat(event.getPayload()).isEqualTo(memoryStatusModel);
    }

    @Test
    public void triggerMemoryStatusUpdate_noConnectedClient() {
        given(memoryMonitoringWebSocketHandler.hasConnectedClient()).willReturn(false);

        underTest.triggerMemoryStatusUpdate();

        verifyNoInteractions(eventGatewayProxy);
    }

    @Test
    public void triggerMemoryStatusUpdate() {
        given(memoryMonitoringWebSocketHandler.hasConnectedClient()).willReturn(true);
        given(memoryStatusModelFactory.create(SERVICE_NAME)).willReturn(memoryStatusModel);

        underTest.triggerMemoryStatusUpdate();

        verify(eventGatewayProxy).sendEvent(sendEventRequestArgumentCaptor.capture());
        assertThat(sendEventRequestArgumentCaptor.getValue().getEventName()).isEqualTo(EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE);

        ArgumentCaptor<WebSocketEvent> argumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(memoryMonitoringWebSocketHandler).should().sendToAllRecipients(argumentCaptor.capture());
        WebSocketEvent event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS);
        assertThat(event.getPayload()).isEqualTo(memoryStatusModel);
    }
}