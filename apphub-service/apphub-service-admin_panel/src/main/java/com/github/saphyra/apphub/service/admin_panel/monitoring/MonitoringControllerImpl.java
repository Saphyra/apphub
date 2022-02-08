package com.github.saphyra.apphub.service.admin_panel.monitoring;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.api.admin_panel.server.MonitoringController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import com.github.saphyra.apphub.service.admin_panel.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.admin_panel.ws.ConnectedWsClients;
import com.github.saphyra.apphub.service.admin_panel.ws.WebSocketMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MonitoringControllerImpl implements MonitoringController {
    private final MessageSenderProxy messageSenderProxy;
    private final ConnectedWsClients connectedWsClients;
    private final WebSocketMessageFactory webSocketMessageFactory;
    private final EventGatewayProxy eventGatewayProxy;

    @Override
    public void reportMemoryStatus(MemoryStatusModel memoryStatus) {
        log.info("Memory status report: {}", memoryStatus);

        WebSocketMessage message = webSocketMessageFactory.create(connectedWsClients, WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS, memoryStatus);
        messageSenderProxy.sendToMonitoring(message);
    }

    @Override
    public void triggerMemoryStatusUpdate() {
        if (!connectedWsClients.isEmpty()) {
            SendEventRequest<Void> sendEventRequest = SendEventRequest.<Void>builder()
                .eventName(EmptyEvent.ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE)
                .build();
            eventGatewayProxy.sendEvent(sendEventRequest);
        }
    }
}
