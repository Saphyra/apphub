package com.github.saphyra.apphub.service.admin_panel.ws;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.common.endpoints.AdminPanelEndpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class MemoryMonitoringWebSocketHandler extends AbstractWebSocketHandler {
    public MemoryMonitoringWebSocketHandler(WebSocketHandlerContext context) {
        super(context);
    }

    @Override
    public String getEndpoint() {
        return AdminPanelEndpoints.WS_CONNECTION_ADMIN_PANEL_MEMORY_MONITORING;
    }

    public boolean hasConnectedClient() {
        return !sessions.isEmpty();
    }

    public void sendToAllRecipients(WebSocketEvent event) {
        sessions.values()
            .forEach(webSocketSessionWrapper -> sendEventToSession(webSocketSessionWrapper, event));
    }
}
