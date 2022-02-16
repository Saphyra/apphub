package com.github.saphyra.apphub.service.admin_panel.monitoring;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.api.admin_panel.server.MonitoringController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.monitoring.MemoryStatusModelFactory;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import com.github.saphyra.apphub.service.admin_panel.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.admin_panel.ws.ConnectedWsClients;
import com.github.saphyra.apphub.service.admin_panel.ws.WebSocketMessageFactory;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@Builder
@RestController
@Slf4j
public class MonitoringControllerImpl implements MonitoringController {
    private final MessageSenderProxy messageSenderProxy;
    private final ConnectedWsClients connectedWsClients;
    private final WebSocketMessageFactory webSocketMessageFactory;
    private final EventGatewayProxy eventGatewayProxy;
    private final MemoryStatusModelFactory memoryStatusModelFactory;
    private final String serviceName;

    public MonitoringControllerImpl(
        MessageSenderProxy messageSenderProxy,
        ConnectedWsClients connectedWsClients,
        WebSocketMessageFactory webSocketMessageFactory,
        EventGatewayProxy eventGatewayProxy,
        MemoryStatusModelFactory memoryStatusModelFactory,
        @Value("${spring.application.name}") String serviceName
    ) {
        this.messageSenderProxy = messageSenderProxy;
        this.connectedWsClients = connectedWsClients;
        this.webSocketMessageFactory = webSocketMessageFactory;
        this.eventGatewayProxy = eventGatewayProxy;
        this.memoryStatusModelFactory = memoryStatusModelFactory;
        this.serviceName = serviceName;
    }

    @Override
    public void reportMemoryStatus(MemoryStatusModel memoryStatus) {
        log.info("Memory status report: {}", memoryStatus);

        WebSocketMessage message = webSocketMessageFactory.create(
            connectedWsClients,
            WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS,
            memoryStatus
        );
        messageSenderProxy.sendToMonitoring(message);
    }

    @Override
    public void triggerMemoryStatusUpdate() {
        if (!connectedWsClients.isEmpty()) {
            log.info("Triggering Memory status update");
            SendEventRequest<Void> sendEventRequest = SendEventRequest.<Void>builder()
                .eventName(EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE)
                .build();
            eventGatewayProxy.sendEvent(sendEventRequest);
            reportMemoryStatus(memoryStatusModelFactory.create(serviceName));
        }
    }
}
